package com.qfu.matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderList;
import quickfix.fix44.NewOrderSingle;

import java.math.BigDecimal;
import java.util.Date;

import static com.qfu.matcher.Group.group;
import static com.qfu.matcher.Header.header;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author mtymes
 * @since 9/29/13 8:18 PM
 */
public class FIXMessageMatcherTest {

    @Test
    public void shouldMatchBasedOnFIXMessageType() {
        // Given
        NewOrderSingle message = new NewOrderSingle();

        // When & Then
        assertThat(message, new FIXMessageMatcher().ofType(NewOrderSingle.class));
        assertThat(message, new FIXMessageMatcher().ofType(Message.class));
        assertThat(message, not(new FIXMessageMatcher().ofType(ExecutionReport.class)));
    }

    @Test
    public void shouldNotBeAllowedToDefineMessageTypeTwice() {
        try {
            new FIXMessageMatcher().ofType(NewOrderList.class).ofType(NewOrderList.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // this is expected
        }
    }

    @Test
    public void shouldMatchFIXMessageBasedOnFieldValues() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.set(new ClOrdID("clOrdId-123")); // String field
        message.set(new Side(Side.BUY)); // char field
        message.set(new PriceType(PriceType.FIXED_AMOUNT)); // int field
        message.set(new Price(1.25d)); // double/decimal field
        Date now = new Date();
        message.set(new TransactTime(now)); // Date field
        message.set(new SolicitedFlag(false)); // boolean field

        // When & Then
        // String
        assertThat(message, new FIXMessageMatcher().with(ClOrdID.FIELD, "clOrdId-123"));
        assertThat(message, not(new FIXMessageMatcher().with(ClOrdID.FIELD, "clOrdId-456")));
        // char
        assertThat(message, new FIXMessageMatcher().with(Side.FIELD, Side.BUY));
        assertThat(message, not(new FIXMessageMatcher().with(Side.FIELD, Side.SELL)));
        // int
        assertThat(message, new FIXMessageMatcher().with(PriceType.FIELD, PriceType.FIXED_AMOUNT));
        assertThat(message, not(new FIXMessageMatcher().with(PriceType.FIELD, PriceType.DISCOUNT)));
        // double
        assertThat(message, new FIXMessageMatcher().with(Price.FIELD, 1.25d));
        assertThat(message, not(new FIXMessageMatcher().with(Price.FIELD, 3.5d)));
        // BigDecimal
        assertThat(message, new FIXMessageMatcher().with(Price.FIELD, new BigDecimal("1.25")));
        assertThat(message, not(new FIXMessageMatcher().with(Price.FIELD, new BigDecimal("3.5"))));
        // Date
        assertThat(message, new FIXMessageMatcher().with(TransactTime.FIELD, now));
        assertThat(message, not(new FIXMessageMatcher().with(TransactTime.FIELD, new Date(now.getTime() + 100L))));
        // boolean
        assertThat(message, new FIXMessageMatcher().with(SolicitedFlag.FIELD, false));
        assertThat(message, not(new FIXMessageMatcher().with(SolicitedFlag.FIELD, true)));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnHeaderFieldValues() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        Message.Header header = message.getHeader();
        header.setField(new SenderSubID("senderSubId-123"));
        // TODO: add remaining data types

        // When & Then
        assertThat(message, new FIXMessageMatcher().withHeaderField(SenderSubID.FIELD, "senderSubId-123"));
        assertThat(message, new FIXMessageMatcher().with(header().with(SenderSubID.FIELD, "senderSubId-123")));
        assertThat(message, not(new FIXMessageMatcher().withHeaderField(SenderSubID.FIELD, "senderSubId-456")));
        assertThat(message, not(new FIXMessageMatcher().with(header().with(SenderSubID.FIELD, "senderSubId-456"))));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnExistenceOfGroup() {
        // Given
        Message message = new NewOrderList();

        message.addGroup(new NewOrderList.NoOrders());
        message.addGroup(new NewOrderList.NoOrders());

        // When & Then
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD)));
        assertThat(message, new FIXMessageMatcher().with(group(2, NoOrders.FIELD)));
        assertThat(message, not(new FIXMessageMatcher().with(group(3, NoOrders.FIELD))));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnGroupFieldValue() {
        // Given
        Message message = new NewOrderList();
        NewOrderList.NoOrders group = new NewOrderList.NoOrders();
        group.set(new ClOrdID("clOrdId-123")); // String field
        group.set(new Side(Side.BUY)); // char field
        group.set(new PriceType(PriceType.FIXED_AMOUNT)); // int field
        group.set(new Price(1.25d)); // double/decimal field
        Date now = new Date();
        group.set(new TransactTime(now)); // Date field
        group.set(new SolicitedFlag(false));
        message.addGroup(group);

        // When & Then
        // String
        assertThat(message, new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123"));
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(ClOrdID.FIELD, "clOrdId-123")));
        assertThat(message, not(new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-456")));
        assertThat(message, not(new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(ClOrdID.FIELD, "clOrdId-456"))));
        // char
        assertThat(message, new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, Side.FIELD, Side.BUY));
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(Side.FIELD, Side.BUY)));
        assertThat(message, not(new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, Side.FIELD, Side.SELL)));
        assertThat(message, not(new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(Side.FIELD, Side.SELL))));
        // int
        assertThat(message, new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, PriceType.FIELD, PriceType.FIXED_AMOUNT));
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(PriceType.FIELD, PriceType.FIXED_AMOUNT)));
        assertThat(message, not(new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, PriceType.FIELD, PriceType.DISCOUNT)));
        assertThat(message, not(new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(PriceType.FIELD, PriceType.DISCOUNT))));
        // double
        assertThat(message, new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, Price.FIELD, 1.25d));
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(Price.FIELD, 1.25d)));
        assertThat(message, not(new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, Price.FIELD, 3.5d)));
        assertThat(message, not(new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(Price.FIELD, 3.5d))));
        // BigDecimal
        assertThat(message, new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, Price.FIELD, new BigDecimal("1.25")));
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(Price.FIELD, new BigDecimal("1.25"))));
        assertThat(message, not(new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, Price.FIELD, new BigDecimal("3.5"))));
        assertThat(message, not(new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(Price.FIELD, new BigDecimal("3.5")))));
        // Date
        assertThat(message, new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, TransactTime.FIELD, now));
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(TransactTime.FIELD, now)));
        assertThat(message, not(new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, TransactTime.FIELD, new Date(now.getTime() + 100L))));
        assertThat(message, not(new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(TransactTime.FIELD, new Date(now.getTime() + 100L)))));
        // BigDecimal
        assertThat(message, new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, SolicitedFlag.FIELD, false));
        assertThat(message, new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(SolicitedFlag.FIELD, false)));
        assertThat(message, not(new FIXMessageMatcher().withGroupField(1, NoOrders.FIELD, SolicitedFlag.FIELD, true)));
        assertThat(message, not(new FIXMessageMatcher().with(group(1, NoOrders.FIELD).with(SolicitedFlag.FIELD, true))));
    }

    @Test
    public void shouldMatchFIXMessageWithComplexMatchCriteria() {
        // Given
        Date now = new Date();
        NewOrderList message = new NewOrderList();
        message.getHeader().setField(new SenderSubID("senderSubId-123"));
        message.set(new ListID("listId-123"));
        message.set(new BidType(BidType.NON_DISCLOSED));
        message.set(new TotNoOrders(1));
        NewOrderList.NoOrders group = new NewOrderList.NoOrders();
        group.set(new ClOrdID("clOrdId-123"));
        group.set(new Side(Side.SELL));
        group.set(new TransactTime(now));
        group.set(new OrdType(OrdType.FOREX_MARKET));
        message.addGroup(group);

        // When & Then
        assertThat(message, new FIXMessageMatcher()
                .ofType(NewOrderList.class)
                .withHeaderField(SenderSubID.FIELD, "senderSubId-123")
                .with(ListID.FIELD, "listId-123")
                .with(BidType.FIELD, BidType.NON_DISCLOSED)
                .with(TotNoOrders.FIELD, 1)
                .withGroupField(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123")
                .withGroupField(1, NoOrders.FIELD, Side.FIELD, Side.SELL)
                .withGroupField(1, NoOrders.FIELD, TransactTime.FIELD, now)
                .withGroupField(1, NoOrders.FIELD, OrdType.FIELD, OrdType.FOREX_MARKET)
        );

        assertThat(message, new FIXMessageMatcher()
                .ofType(NewOrderList.class)
                .with(header().with(SenderSubID.FIELD, "senderSubId-123"))
                .with(ListID.FIELD, "listId-123")
                .with(BidType.FIELD, BidType.NON_DISCLOSED)
                .with(TotNoOrders.FIELD, 1)
                .with(group(1, NoOrders.FIELD)
                        .with(ClOrdID.FIELD, "clOrdId-123")
                        .with(Side.FIELD, Side.SELL)
                        .with(TransactTime.FIELD, now)
                        .with(OrdType.FIELD, OrdType.FOREX_MARKET)
                )
        );
    }

    // TODO: add message text test
//    assertThat(message, isFixMessage(NewOrderList.class)
//            .with(header().with(SenderSubID.FIELD, "senderSubId-123"))
//            .with(ListID.FIELD, "listId-123")
//            .with(BidType.FIELD, BidType.NON_DISCLOSED)
//            .with(TotNoOrders.FIELD, 1)
//            .with(group(1, NoOrders.FIELD)
//                    .with(ClOrdID.FIELD, "clOrdId-123")
//                    .with(Side.FIELD, Side.SELL)
//                    .with(TransactTime.FIELD, now)
//                    .with(OrdType.FIELD, OrdType.FOREX_MARKET)
//                    .with(ClOrdID.FIELD, "otherClOrdID")
//            )
//            .with(group(2, NoOrders.FIELD))
//    );
}

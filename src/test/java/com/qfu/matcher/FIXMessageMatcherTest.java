package com.qfu.matcher;

import org.junit.Test;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.Message;
import quickfix.fix44.NewOrderList;
import quickfix.fix44.NewOrderSingle;

import java.math.BigDecimal;
import java.util.Date;

import static com.qfu.matcher.FIXMessageMatcher.isFixMessage;
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
        assertThat(message, isFixMessage(NewOrderSingle.class));
        assertThat(message, isFixMessage().ofType(NewOrderSingle.class));
        assertThat(message, isFixMessage(Message.class));
        assertThat(message, isFixMessage().ofType(Message.class));
        assertThat(message, not(isFixMessage(ExecutionReport.class)));
        assertThat(message, not(isFixMessage().ofType(ExecutionReport.class)));
    }

    @Test
    public void shouldNotBeAllowedToDefineMessageTypeTwice() {
        try {
            isFixMessage(NewOrderList.class).ofType(NewOrderList.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // this is expected
        }

        try {
            isFixMessage().ofType(NewOrderList.class).ofType(NewOrderList.class);
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
        assertThat(message, isFixMessage().with(ClOrdID.FIELD, "clOrdId-123"));
        assertThat(message, not(isFixMessage().with(ClOrdID.FIELD, "clOrdId-456")));
        // char
        assertThat(message, isFixMessage().with(Side.FIELD, Side.BUY));
        assertThat(message, not(isFixMessage().with(Side.FIELD, Side.SELL)));
        // int
        assertThat(message, isFixMessage().with(PriceType.FIELD, PriceType.FIXED_AMOUNT));
        assertThat(message, not(isFixMessage().with(PriceType.FIELD, PriceType.DISCOUNT)));
        // double
        assertThat(message, isFixMessage().with(Price.FIELD, 1.25d));
        assertThat(message, not(isFixMessage().with(Price.FIELD, 3.5d)));
        // BigDecimal
        assertThat(message, isFixMessage().with(Price.FIELD, new BigDecimal("1.25")));
        assertThat(message, not(isFixMessage().with(Price.FIELD, new BigDecimal("3.5"))));
        // Date
        assertThat(message, isFixMessage().with(TransactTime.FIELD, now));
        assertThat(message, not(isFixMessage().with(TransactTime.FIELD, new Date(now.getTime() + 100L))));
        // boolean
        assertThat(message, isFixMessage().with(SolicitedFlag.FIELD, false));
        assertThat(message, not(isFixMessage().with(SolicitedFlag.FIELD, true)));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnStringHeaderValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.getHeader().setField(new SenderSubID("senderSubId-123"));

        // When & Then
        assertThat(message, isFixMessage().withHeaderField(SenderSubID.FIELD, "senderSubId-123"));
        assertThat(message, isFixMessage().with(header().with(SenderSubID.FIELD, "senderSubId-123")));
        assertThat(message, not(isFixMessage().withHeaderField(SenderSubID.FIELD, "senderSubId-456")));
        assertThat(message, not(isFixMessage().with(header().with(SenderSubID.FIELD, "senderSubId-456"))));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnExistenceOfGroup() {
        // Given
        Message message = new NewOrderList();

        NewOrderList.NoOrders group = new NewOrderList.NoOrders();
        message.addGroup(group);

        // When & Then
        assertThat(message, isFixMessage().with(group(1, NoOrders.FIELD)));
        assertThat(message, not(isFixMessage().with(group(2, NoOrders.FIELD))));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnStringGroupValue() {
        // Given
        Message message = new NewOrderList();

        NewOrderList.NoOrders group = new NewOrderList.NoOrders();
        group.set(new ClOrdID("clOrdId-123"));
        message.addGroup(group);

        // When & Then
        assertThat(message, isFixMessage().withGroupField(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123"));
        assertThat(message, isFixMessage().with(group(1, NoOrders.FIELD).with(ClOrdID.FIELD, "clOrdId-123")));
        assertThat(message, not(isFixMessage().withGroupField(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-456")));
        assertThat(message, not(isFixMessage().with(group(1, NoOrders.FIELD).with(ClOrdID.FIELD, "clOrdId-456"))));
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
        assertThat(message, isFixMessage(NewOrderList.class)
                .withHeaderField(SenderSubID.FIELD, "senderSubId-123")
                .with(ListID.FIELD, "listId-123")
                .with(BidType.FIELD, BidType.NON_DISCLOSED)
                .with(TotNoOrders.FIELD, 1)
                .withGroupField(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123")
                .withGroupField(1, NoOrders.FIELD, Side.FIELD, Side.SELL)
                .withGroupField(1, NoOrders.FIELD, TransactTime.FIELD, now)
                .withGroupField(1, NoOrders.FIELD, OrdType.FIELD, OrdType.FOREX_MARKET)
        );

        assertThat(message, isFixMessage(NewOrderList.class)
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

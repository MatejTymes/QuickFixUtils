package com.matejtymes.qfu.matcher;

import org.junit.Test;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.Message;
import quickfix.fix44.NewOrderList;
import quickfix.fix44.NewOrderSingle;

import java.util.Date;

import static com.matejtymes.qfu.matcher.FIXMessageMatcher.isFixMessage;
import static com.matejtymes.qfu.matcher.Group.group;
import static com.matejtymes.qfu.matcher.Header.header;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

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
        assertThat(message, not(isFixMessage(ExecutionReport.class)));
        assertThat(message, not(isFixMessage().ofType(ExecutionReport.class)));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.set(new ClOrdID("clOrdId-123"));

        // When & Then
        assertThat(message, isFixMessage().with(ClOrdID.FIELD, "clOrdId-123"));
        assertThat(message, not(isFixMessage().with(ClOrdID.FIELD, "clOrdId-456")));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnStringHeaderValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.getHeader().setField(new SenderSubID("senderSubId-123"));

        // When & Then
        assertThat(message, isFixMessage().withHeader(SenderSubID.FIELD, "senderSubId-123"));
        assertThat(message, isFixMessage().with(header().with(SenderSubID.FIELD, "senderSubId-123")));
        assertThat(message, not(isFixMessage().withHeader(SenderSubID.FIELD, "senderSubId-456")));
        assertThat(message, not(isFixMessage().with(header().with(SenderSubID.FIELD, "senderSubId-456"))));
    }

    @Test
    public void shouldMatchFIXMessageBasedOnStringGroupValue() {
        // Given
        Message message = new NewOrderList();

        NewOrderList.NoOrders group = new NewOrderList.NoOrders();
        group.set(new ClOrdID("clOrdId-123"));
        message.addGroup(group);

        // When & Then
        assertThat(message, isFixMessage().withGroup(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123"));
        assertThat(message, isFixMessage().with(group(1, NoOrders.FIELD).with(ClOrdID.FIELD, "clOrdId-123")));
        assertThat(message, not(isFixMessage().withGroup(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-456")));
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
                .withHeader(SenderSubID.FIELD, "senderSubId-123")
                .with(ListID.FIELD, "listId-123")
                .with(BidType.FIELD, BidType.NON_DISCLOSED)
                .with(TotNoOrders.FIELD, 1)
                .withGroup(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123")
                .withGroup(1, NoOrders.FIELD, Side.FIELD, Side.SELL)
                .withGroup(1, NoOrders.FIELD, TransactTime.FIELD, now)
                .withGroup(1, NoOrders.FIELD, OrdType.FIELD, OrdType.FOREX_MARKET)
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
}

package com.matejtymes.qfu.matcher;

import org.junit.Test;
import quickfix.field.*;
import quickfix.fix44.Message;
import quickfix.fix44.NewOrderList;
import quickfix.fix44.NewOrderSingle;

import java.util.Date;

import static com.matejtymes.qfu.matcher.FIXMessageMatcher.isFixMessage;
import static com.matejtymes.qfu.matcher.Group.group;
import static com.matejtymes.qfu.matcher.Header.header;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author mtymes
 * @since 9/29/13 8:18 PM
 */
public class FIXMessageMatcherTest {

    @Test
    public void shouldMatchFIXMessageOfTheSameType() {
        // Given
        Message message = new NewOrderSingle();

        // When
        FIXMessageMatcher matcher = isFixMessage().ofType(NewOrderSingle.class);

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldNotMatchFIXMessageOfDifferentType() {
        // Given
        Message message = new NewOrderSingle();

        // When
        FIXMessageMatcher matcher = isFixMessage().ofType(NewOrderList.class);

        // Then
        assertThat(matcher.matchesSafely(message), is(false));
    }

    @Test
    public void shouldMatchFIXMessageWithSameStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.set(new ClOrdID("clOrdId-123"));

        // When
        FIXMessageMatcher matcher = isFixMessage().with(ClOrdID.FIELD, "clOrdId-123");

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldNotMatchFIXMessageWithDifferentStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.set(new ClOrdID("clOrdId-123"));

        // When
        FIXMessageMatcher matcher = isFixMessage().with(ClOrdID.FIELD, "clOdrId-456");

        // Then
        assertThat(matcher.matchesSafely(message), is(false));
    }

    @Test
    public void shouldMatchFIXMessageWithSameHeaderStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.getHeader().setField(new SenderSubID("senderSubId-123"));

        // When
        FIXMessageMatcher matcher = isFixMessage().withHeader(SenderSubID.FIELD, "senderSubId-123");

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldNotMatchFIXMessageWithDifferentHeaderStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.getHeader().setField(new SenderSubID("senderSubId-123"));

        // When
        FIXMessageMatcher matcher = isFixMessage().withHeader(SenderSubID.FIELD, "senderSubId-456");

        // Then
        assertThat(matcher.matchesSafely(message), is(false));
    }

    @Test
    public void shouldMatchFIXMessageWithSameGroupStringValue() {
        // Given
        Message message = new NewOrderList();
        NewOrderList.NoOrders group = new NewOrderList.NoOrders();
        group.set(new ClOrdID("clOrdId-123"));
        message.addGroup(group);

        // When
        FIXMessageMatcher matcher = isFixMessage().withGroup(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123");

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldNotMatchFIXMessageWithDifferentGroupStringValue() {
        // Given
        Message message = new NewOrderList();
        NewOrderList.NoOrders group = new NewOrderList.NoOrders();
        group.set(new ClOrdID("clOrdId-123"));
        message.addGroup(group);

        // When
        FIXMessageMatcher matcher = isFixMessage().withGroup(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-456");

        // Then
        assertThat(matcher.matchesSafely(message), is(false));
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

        // When
        FIXMessageMatcher matcher = isFixMessage(NewOrderList.class)
                .withHeader(SenderSubID.FIELD, "senderSubId-123")
                .with(ListID.FIELD, "listId-123")
                .with(BidType.FIELD, BidType.NON_DISCLOSED)
                .with(TotNoOrders.FIELD, 1)
                .withGroup(1, NoOrders.FIELD, ClOrdID.FIELD, "clOrdId-123")
                .withGroup(1, NoOrders.FIELD, Side.FIELD, Side.SELL)
                .withGroup(1, NoOrders.FIELD, TransactTime.FIELD, now)
                .withGroup(1, NoOrders.FIELD, OrdType.FIELD, OrdType.FOREX_MARKET);

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldMatchFIXMessageWithComplexMatchCriteriaV2() {
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

        // When
        FIXMessageMatcher matcher = isFixMessage(NewOrderList.class)
                .with(header().with(SenderSubID.FIELD, "senderSubId-123"))
                .with(ListID.FIELD, "listId-123")
                .with(BidType.FIELD, BidType.NON_DISCLOSED)
                .with(TotNoOrders.FIELD, 1)
                .with(group(1, NoOrders.FIELD)
                        .with(ClOrdID.FIELD, "clOrdId-123")
                        .with(Side.FIELD, Side.SELL)
                        .with(TransactTime.FIELD, now)
                        .with(OrdType.FIELD, OrdType.FOREX_MARKET)
                );

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }
}

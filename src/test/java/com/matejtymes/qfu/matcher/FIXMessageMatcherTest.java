package com.matejtymes.qfu.matcher;

import org.junit.Test;
import quickfix.field.*;
import quickfix.fix44.Message;
import quickfix.fix44.NewOrderList;
import quickfix.fix44.NewOrderSingle;

import java.util.Date;

import static com.matejtymes.qfu.matcher.FIXMessageMatcher.fixMessageOfType;
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
        FIXMessageMatcher<NewOrderSingle> matcher = fixMessageOfType(NewOrderSingle.class);

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldNotMatchFIXMessageOfDifferentType() {
        // Given
        Message message = new NewOrderSingle();

        // When
        FIXMessageMatcher<NewOrderList> matcher = fixMessageOfType(NewOrderList.class);

        // Then
        assertThat(matcher.matchesSafely(message), is(false));
    }

    @Test
    public void shouldMatchFIXMessageWithSameStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.set(new ClOrdID("clOrdId-123"));

        // When
        FIXMessageMatcher<NewOrderSingle> matcher = fixMessageOfType(NewOrderSingle.class)
                .with(ClOrdID.FIELD, "clOrdId-123");

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldNotMatchFIXMessageWithDifferentStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.set(new ClOrdID("clOrdId-123"));

        // When
        FIXMessageMatcher<NewOrderSingle> matcher = fixMessageOfType(NewOrderSingle.class)
                .with(ClOrdID.FIELD, "clOdrId-456");

        // Then
        assertThat(matcher.matchesSafely(message), is(false));
    }

    @Test
    public void shouldMatchFIXMessageWithSameHeaderStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.getHeader().setField(new SenderSubID("senderSubId-123"));

        // When
        FIXMessageMatcher<NewOrderSingle> matcher = fixMessageOfType(NewOrderSingle.class)
                .withHeader(SenderSubID.FIELD, "senderSubId-123");

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }

    @Test
    public void shouldNotMatchFIXMessageWithDifferentHeaderStringValue() {
        // Given
        NewOrderSingle message = new NewOrderSingle();
        message.getHeader().setField(new SenderSubID("senderSubId-123"));

        // When
        FIXMessageMatcher<NewOrderSingle> matcher = fixMessageOfType(NewOrderSingle.class)
                .withHeader(SenderSubID.FIELD, "senderSubId-456");

        // Then
        assertThat(matcher.matchesSafely(message), is(false));
    }

    @Test
    public void shouldMatchFIXMessageWithComplexMatchCriteria() {
        // Given
        Date now = new Date();
        Message message = new NewOrderSingle(
                new ClOrdID("clrOdrId-123"),
                new Side(Side.SELL),
                new TransactTime(now),
                new OrdType(OrdType.FOREX_MARKET)
        );
        message.getHeader().setField(new SenderSubID("senderSubId-123"));

        // When
        FIXMessageMatcher<NewOrderSingle> matcher = fixMessageOfType(NewOrderSingle.class)
                .withHeader(SenderSubID.FIELD, "senderSubId-123")
//                .with(header().with(SenderSubID.FIELD, "senderSubId-456"))
                .with(ClOrdID.FIELD, "clrOdrId-123")
                .with(Side.FIELD, Side.SELL)
                .with(TransactTime.FIELD, now)
                .with(OrdType.FIELD, OrdType.FOREX_MARKET);

        // Then
        assertThat(matcher.matchesSafely(message), is(true));
    }
}

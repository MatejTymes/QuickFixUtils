package com.qfu.matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderList;
import quickfix.fix44.NewOrderSingle;

import java.util.Date;

import static com.qfu.matcher.Group.group;
import static com.qfu.matcher.Header.header;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * @author mtymes
 * @since 9/29/13 8:18 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class FIXMessageMatcherTest {

    private Message message = new Message();
    private Message.Header header = message.getHeader();

    @Mock
    private FieldMatcher fieldMatcher;

    @Test
    public void shouldMatchFIXMessageType() {
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
    public void shouldMatchFieldObject() {
        // Given
        when(fieldMatcher.hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(true);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(new ClOrdID("clOrdId-123"))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(true));

        verify(fieldMatcher).hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldNotMatchFieldObject() {
        // Given
        when(fieldMatcher.hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(false);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(new ClOrdID("clOrdId-123"))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(false));

        verify(fieldMatcher).hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldMatchFieldValue() {
        // Given
        when(fieldMatcher.hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(true);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(ClOrdID.FIELD, "clOrdId-123")
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(true));

        verify(fieldMatcher).hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldNotMatchFieldValue() {
        // Given
        when(fieldMatcher.hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(false);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(ClOrdID.FIELD, "clOrdId-123")
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(false));

        verify(fieldMatcher).hasFieldValue(message, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldMatchHeaderFieldObject() {
        // Given
        when(fieldMatcher.hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"))).thenReturn(true);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(header().with(new SenderSubID("senderSubId-123")))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(true));

        verify(fieldMatcher).hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldNotMatchHeaderFieldObject() {
        // Given
        when(fieldMatcher.hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"))).thenReturn(false);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(header().with(new SenderSubID("senderSubId-123")))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(false));

        verify(fieldMatcher).hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldMatchHeaderFieldValue() {
        // Given
        when(fieldMatcher.hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"))).thenReturn(true);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(header().with(SenderSubID.FIELD, "senderSubId-123"))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(true));

        verify(fieldMatcher).hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldNotMatchHeaderFieldValue() {
        // Given
        when(fieldMatcher.hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"))).thenReturn(false);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(header().with(SenderSubID.FIELD, "senderSubId-123"))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(false));

        verify(fieldMatcher).hasFieldValue(header, new FieldValue(SenderSubID.FIELD, "senderSubId-123"));
        verifyNoMoreInteractions(fieldMatcher);
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
    public void shouldMatchGroupFieldObject() throws FieldNotFound {
        // Given
        message.addGroup(new NewOrderList.NoOrders());
        quickfix.Group group = message.getGroup(1, NoOrders.FIELD);

        when(fieldMatcher.hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(true);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(group(1, NoOrders.FIELD)
                        .with(new ClOrdID("clOrdId-123")))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(true));

        verify(fieldMatcher).hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldNotMatchGroupFieldObject() throws FieldNotFound {
        // Given
        message.addGroup(new NewOrderList.NoOrders());
        quickfix.Group group = message.getGroup(1, NoOrders.FIELD);

        when(fieldMatcher.hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(false);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(group(1, NoOrders.FIELD)
                        .with(new ClOrdID("clOrdId-123")))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(false));

        verify(fieldMatcher).hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldMatchGroupFieldValue() throws FieldNotFound {
        // Given
        message.addGroup(new NewOrderList.NoOrders());
        quickfix.Group group = message.getGroup(1, NoOrders.FIELD);

        when(fieldMatcher.hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(true);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(group(1, NoOrders.FIELD)
                        .with(ClOrdID.FIELD, "clOrdId-123"))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(true));

        verify(fieldMatcher).hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
    }

    @Test
    public void shouldNotMatchGroupFieldValue() throws FieldNotFound {
        // Given
        message.addGroup(new NewOrderList.NoOrders());
        quickfix.Group group = message.getGroup(1, NoOrders.FIELD);

        when(fieldMatcher.hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"))).thenReturn(false);

        // When
        boolean matchesMessage = new FIXMessageMatcher(fieldMatcher)
                .with(group(1, NoOrders.FIELD)
                        .with(ClOrdID.FIELD, "clOrdId-123"))
                .matchesSafely(message);

        // Then
        assertThat(matchesMessage, is(false));

        verify(fieldMatcher).hasFieldValue(group, new FieldValue(ClOrdID.FIELD, "clOrdId-123"));
        verifyNoMoreInteractions(fieldMatcher);
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

        assertThat(message, new FIXMessageMatcher()
                .ofType(NewOrderList.class)
                .with(header().with(new SenderSubID("senderSubId-123")))
                .with(new ListID("listId-123"))
                .with(new BidType(BidType.NON_DISCLOSED))
                .with(new TotNoOrders(1))
                .with(group(1, NoOrders.FIELD)
                        .with(new ClOrdID("clOrdId-123"))
                        .with(new Side(Side.SELL))
                        .with(new TransactTime(now))
                        .with(new OrdType(OrdType.FOREX_MARKET))
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

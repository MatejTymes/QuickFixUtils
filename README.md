QuickFixUtils
=============

QuickFix/J utility library contains:

* Hamcrest matchers

Hamcrest Matchers
=================

This utility provides hamcrest matchers for FIX message matching. Usage is as follows:

<code>
import static com.qfu.matcher.FIXMatchers.isFIXMessage;
</code>

<code>
        assertThat(message, isFIXMessage()
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
</code>
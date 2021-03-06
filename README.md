QuickFixUtils
=============

QuickFix/J utility library contains:

* Hamcrest matchers

Hamcrest Matchers
=================

This utility provides hamcrest matchers for FIX message matching. Usage is as follows:

```java
...
import static com.qfu.matcher.FIXMatchers.isFIXMessage;
import static com.qfu.matcher.Group.group;
import static com.qfu.matcher.Header.header;
...

        // use field id and value
        assertThat(message, isFIXMessage()
                .ofType(NewOrderList.class)
                .with(header().with(SenderSubID.FIELD, "senderSubId-123"))
                .with(ListID.FIELD, "listId-123")
                .with(BidType.FIELD, BidType.NON_DISCLOSED)
                .with(TotNoOrders.FIELD, 1)
                .with(group(1, NoOrders.FIELD)
                        .with(ClOrdID.FIELD, "clOrdId-123")
                        .with(Side.FIELD, Side.SELL)
                        .with(Price.FIELD, new BigDecimal("1.25"))
                        .with(TransactTime.FIELD, new Date())
                        .with(OrdType.FIELD, OrdType.FOREX_MARKET)
                )
        );

        // or field object with value
        assertThat(message, isFIXMessage()
                .ofType(NewOrderList.class)
                .with(header().with(new SenderSubID("senderSubId-123")))
                .with(new ListID("listId-123"))
                .with(new BidType(BidType.NON_DISCLOSED))
                .with(new TotNoOrders(1))
                .with(group(1, NoOrders.FIELD)
                        .with(new ClOrdID("clOrdId-123"))
                        .with(new Side(Side.SELL))
                        .with(new Price(new BigDecimal("1.25")))
                        .with(new TransactTime(new Date()))
                        .with(new OrdType(OrdType.FOREX_MARKET))
                )
        );
```
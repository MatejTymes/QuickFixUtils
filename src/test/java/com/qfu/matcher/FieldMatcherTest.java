package com.qfu.matcher;

import org.junit.Test;
import quickfix.FieldMap;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author mtymes
 * @since 10/22/13 11:23 PM
 */
public class FieldMatcherTest {

    private final FieldMap fieldMap = new Message();

    private FieldMatcher fieldMatcher = new FieldMatcher();

    @Test
    public void shouldFindFieldIsMissing() {
        // When & Then
        // String
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(ClOrdID.FIELD, "clOrdId-123")), is(false));
        // char
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Side.FIELD, Side.BUY)), is(false));
        // int
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(NumDaysInterest.FIELD, 3)), is(false));
        // double
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Price.FIELD, 1.25d)), is(false));
        // BigDecimal
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Price.FIELD, new BigDecimal("1.25"))), is(false));
        // Date
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(TransactTime.FIELD, new Date())), is(false));
        // boolean
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(SolicitedFlag.FIELD, false)), is(false));
    }

    @Test
    public void shouldFindIfHasStringFieldValue() {
        // Given
        fieldMap.setField(new ClOrdID("clOrdId-123"));

        // When & Then
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(ClOrdID.FIELD, "clOrdId-123")), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(ClOrdID.FIELD, "clOrdId-456")), is(false));
    }

    @Test
    public void shouldFindIfHasCharacterFieldValue() {
        // Given
        fieldMap.setField(new Side(Side.BUY));

        // When & Then
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Side.FIELD, Side.BUY)), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Side.FIELD, Side.SELL)), is(false));
    }

    @Test
    public void shouldFindIfHasIntegerFieldValue() {
        // Given
        fieldMap.setField(new PriceType(PriceType.FIXED_AMOUNT));
        fieldMap.setField(new NumDaysInterest(3));

        // When & Then
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(PriceType.FIELD, PriceType.FIXED_AMOUNT)), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(PriceType.FIELD, PriceType.DISCOUNT)), is(false));

        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(NumDaysInterest.FIELD, 3)), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(NumDaysInterest.FIELD, 10)), is(false));
    }

    @Test
    public void shouldFindIfHasDoubleFieldValue() {
        // Given
        fieldMap.setField(new Price(1.25d));

        // When & Then
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Price.FIELD, 1.25d)), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Price.FIELD, 3.5d)), is(false));
    }

    @Test
    public void shouldFindIfHasBigDecimalFieldValue() {
        // Given
        fieldMap.setField(new Price(1.25d));

        // When & Then
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Price.FIELD, new BigDecimal("1.25"))), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(Price.FIELD, new BigDecimal("3.5"))), is(false));
    }

    @Test
    public void shouldFindIfHasDateFieldValue() {
        // Given
        Date now = new Date();
        fieldMap.setField(new TransactTime(now));

        // When & Then
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(TransactTime.FIELD, now)), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(TransactTime.FIELD, new Date(now.getTime() + 100L))), is(false));
    }

    @Test
    public void shouldFindIfHasBooleanFieldValue() {
        // Given
        fieldMap.setField(new SolicitedFlag(false));

        // When & Then
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(SolicitedFlag.FIELD, false)), is(true));
        assertThat(fieldMatcher.hasFieldValue(fieldMap, fieldValue(SolicitedFlag.FIELD, true)), is(false));
    }

    /* ============================== */
    /* ---     helper methods     --- */
    /* ============================== */


    private FieldValue fieldValue(int fieldId, Object value) {
        return new FieldValue(fieldId, value);
    }
}

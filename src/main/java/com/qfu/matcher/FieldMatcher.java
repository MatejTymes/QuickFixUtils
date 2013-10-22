package com.qfu.matcher;

import quickfix.FieldMap;
import quickfix.FieldNotFound;

import java.math.BigDecimal;
import java.util.Date;

import static java.lang.String.format;

/**
 * @author mtymes
 * @since 10/22/13 11:18 PM
 */
class FieldMatcher {

    public final static FieldMatcher INSTANCE = new FieldMatcher();

    public boolean hasFieldValue(FieldMap fieldMap, FieldValue fieldValue) {
        boolean matches;

        int fieldId = fieldValue.getFieldId();
        Object value = fieldValue.getValue();
        if (value instanceof String) {
            matches = hasValue(fieldMap, fieldId, (String) value);
        } else if (value instanceof Character) {
            matches = hasValue(fieldMap, fieldId, (Character) value);
        } else if (value instanceof Integer) {
            matches = hasValue(fieldMap, fieldId, (Integer) value);
        } else if (value instanceof Double) {
            matches = hasValue(fieldMap, fieldId, (Double) value);
        } else if (value instanceof BigDecimal) {
            matches = hasValue(fieldMap, fieldId, (BigDecimal) value);
        } else if (value instanceof Date) {
            matches = hasValue(fieldMap, fieldId, (Date) value);
        } else if (value instanceof Boolean) {
            matches = hasValue(fieldMap, fieldId, (Boolean) value);
        } else {
            throw new IllegalArgumentException(format("unable to process field %d with value type %s", fieldId, value.getClass()));
        }

        return matches;
    }

    /* ============================== */
    /* ---     helper methods     --- */
    /* ============================== */

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, String expectedValue) {
        boolean matches;
        try {
            String actualValue = fieldMap.getString(fieldId);
            matches = expectedValue.equals(actualValue);
        } catch (FieldNotFound e) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, int expectedValue) {
        boolean matches;
        try {
            int actualValue = fieldMap.getInt(fieldId);
            matches = (expectedValue == actualValue);
        } catch (FieldNotFound e) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, char expectedValue) {
        boolean matches;
        try {
            char actualValue = fieldMap.getChar(fieldId);
            matches = (expectedValue == actualValue);
        } catch (FieldNotFound e) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, double expectedValue) {
        boolean matches;
        try {
            double actualValue = fieldMap.getDouble(fieldId);
            matches = (expectedValue == actualValue);
        } catch (FieldNotFound e) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, BigDecimal expectedValue) {
        boolean matches;
        try {
            BigDecimal actualValue = fieldMap.getDecimal(fieldId);
            matches = (expectedValue.compareTo(actualValue) == 0);
        } catch (FieldNotFound fieldNotFound) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, boolean expectedValue) {
        boolean matches;
        try {
            boolean actualValue = fieldMap.getBoolean(fieldId);
            matches = (expectedValue == actualValue);
        } catch (FieldNotFound e) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, Date expectedValue) {
        boolean matches;
        try {
            // FIXME: there are 3 different methods: getUtcTimeStamp, getUtcTimeOnly, getUtcDateOnly - use all of them
            Date actualValue = fieldMap.getUtcTimeStamp(fieldId);
            matches = expectedValue.equals(actualValue);
        } catch (FieldNotFound e) {
            matches = false;
        }
        return matches;
    }
}

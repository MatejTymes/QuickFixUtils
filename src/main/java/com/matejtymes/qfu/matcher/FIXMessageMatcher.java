package com.matejtymes.qfu.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mtymes
 * @since 9/29/13 8:22 PM
 */
// TODO: add group mapping
// TODO: start using field matchers so we can introduce extensions
public class FIXMessageMatcher<T extends Message> extends TypeSafeMatcher<Message> {

    private final Class<T> messageType;
    private final Map<Integer, Object> fieldValues = new HashMap<Integer, Object>();
    private final Map<Integer, Object> headerFieldValues = new HashMap<Integer, Object>();

    public FIXMessageMatcher(Class<T> messageType) {
        this.messageType = messageType;
    }

    public static <T extends Message> FIXMessageMatcher<T> fixMessageOfType(Class<T> messageType) {
        return new FIXMessageMatcher<T>(messageType);
    }

    public FIXMessageMatcher<T> with(int fieldId, Object value) {
        fieldValues.put(fieldId, value);
        return this;
    }

    public FIXMessageMatcher<T> withHeader(int fieldId, Object value) {
        headerFieldValues.put(fieldId, value);
        return this;
    }

    @Override
    protected boolean matchesSafely(Message message) {
        boolean matches = true;

        if (!messageType.isAssignableFrom(message.getClass())) {
            matches = false;
        }

        for (Integer fieldId : fieldValues.keySet()) {
            Object fieldValue = fieldValues.get(fieldId);
            matches &= matchesValue(message, fieldId, fieldValue);
        }

        for (Integer fieldId : headerFieldValues.keySet()) {
            Object fieldValue = headerFieldValues.get(fieldId);
            matches &= matchesValue(message.getHeader(), fieldId, fieldValue);
        }

        // TODO: implement field matching

        return matches;
    }

    @Override
    public void describeTo(Description description) {
        // TODO: implement look at CustomTypeSafeMatcher.describeTo
//        description.appendText(fixedDescription);
    }

    /* ============================== */
    /* ---     helper methods     --- */
    /* ============================== */

    private boolean matchesValue(FieldMap fieldMap, Integer fieldId, Object fieldValue) {
        boolean matches;

        if (fieldValue instanceof String) {
            matches = hasValue(fieldMap, fieldId, (String) fieldValue);
        } else if (fieldValue instanceof Character) {
            matches = hasValue(fieldMap, fieldId, (Character) fieldValue);
        } else if (fieldValue instanceof Integer) {
            matches = hasValue(fieldMap, fieldId, (Integer) fieldValue);
        } else if (fieldValue instanceof Double) {
            matches = hasValue(fieldMap, fieldId, (Double) fieldValue);
        } else if (fieldValue instanceof BigDecimal) {
            matches = hasValue(fieldMap, fieldId, (BigDecimal) fieldValue);
        } else if (fieldValue instanceof Date) {
            matches = hasValue(fieldMap, fieldId, (Date) fieldValue);
        } else if (fieldValue instanceof Boolean) {
            matches = hasValue(fieldMap, fieldId, (Boolean) fieldValue);
        } else {
            throw new IllegalArgumentException(String.format("unable to process field %d with value type %s", fieldId, fieldValue.getClass()));
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, String expectedValue) {
        boolean matches;
        try {
            String actualValue = fieldMap.getString(fieldId);
            matches = expectedValue.equals(actualValue);
        } catch (FieldNotFound fieldNotFound) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, int expectedValue) {
        boolean matches;
        try {
            int actualValue = fieldMap.getInt(fieldId);
            matches = (expectedValue == actualValue);
        } catch (FieldNotFound fieldNotFound) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, char expectedValue) {
        boolean matches;
        try {
            char actualValue = fieldMap.getChar(fieldId);
            matches = (expectedValue == actualValue);
        } catch (FieldNotFound fieldNotFound) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, double expectedValue) {
        boolean matches;
        try {
            double actualValue = fieldMap.getDouble(fieldId);
            matches = (expectedValue == actualValue);
        } catch (FieldNotFound fieldNotFound) {
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
        } catch (FieldNotFound fieldNotFound) {
            matches = false;
        }
        return matches;
    }

    private boolean hasValue(FieldMap fieldMap, Integer fieldId, Date expectedValue) {
        boolean matches;
        try {
            // TODO: there are 3 different methods: getUtcTimeStamp, getUtcTimeOnly, getUtcDateOnly - use all of them
            Date actualValue = fieldMap.getUtcTimeStamp(fieldId);
            matches = expectedValue.equals(actualValue);
        } catch (FieldNotFound fieldNotFound) {
            matches = false;
        }
        return matches;
    }
}

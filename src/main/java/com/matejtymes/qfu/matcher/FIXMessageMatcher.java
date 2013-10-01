package com.matejtymes.qfu.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author mtymes
 * @since 9/29/13 8:22 PM
 */
// TODO: doesn't have to be generic - the messageType can be undefined
// TODO: start using field matchers so we can introduce extensions
public class FIXMessageMatcher<T extends Message> extends TypeSafeMatcher<Message> {

    private final Class<T> messageType;
    private final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
    private final List<FieldValue> headerFieldValues = new ArrayList<FieldValue>();
    private final Map<GroupId, List<FieldValue>> groupFieldValues = new HashMap<GroupId, List<FieldValue>>();

    public FIXMessageMatcher(Class<T> messageType) {
        this.messageType = messageType;
    }

    public static <T extends Message> FIXMessageMatcher<T> isFixMessageOfType(Class<T> messageType) {
        return new FIXMessageMatcher<T>(messageType);
    }

    public FIXMessageMatcher<T> with(int fieldId, Object value) {
        fieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    public FIXMessageMatcher<T> withHeader(int fieldId, Object value) {
        headerFieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    public FIXMessageMatcher<T> with(Header header) {
        headerFieldValues.addAll(header.getFieldValues());
        return this;
    }

    public FIXMessageMatcher<T> withGroup(int groupIndex, int groupTag, int fieldId, Object value) {
        GroupId groupId = new GroupId(groupIndex, groupTag);
        FieldValue fieldValue = new FieldValue(fieldId, value);
        List<FieldValue> fieldValues = groupFieldValues.get(groupId);
        if (fieldValues == null) {
            fieldValues = new ArrayList<FieldValue>();
            groupFieldValues.put(groupId, fieldValues);
        }
        fieldValues.add(fieldValue);
        return this;
    }

    public FIXMessageMatcher<T> with(com.matejtymes.qfu.matcher.Group group) {
        GroupId groupId = group.getGroupId();
        List<FieldValue> fieldValues = groupFieldValues.get(groupId);
        if (fieldValues == null) {
            fieldValues = new ArrayList<FieldValue>();
            groupFieldValues.put(groupId, fieldValues);
        }
        fieldValues.addAll(group.getFieldValues());
        return this;
    }

    @Override
    protected boolean matchesSafely(Message message) {
        boolean matches = true;

        if (!messageType.isAssignableFrom(message.getClass())) {
            matches = false;
        }

        for (FieldValue fieldValue : fieldValues) {
            matches &= hasFieldValue(message, fieldValue);
        }

        Message.Header header = message.getHeader();
        for (FieldValue fieldValue : headerFieldValues) {
            matches &= hasFieldValue(header, fieldValue);
        }

        for (GroupId groupId : groupFieldValues.keySet())
        {
            try {
                Group group = message.getGroup(groupId.getIndex(), groupId.getGroupTag());

                for (FieldValue fieldValue : groupFieldValues.get(groupId)) {
                    matches &= hasFieldValue(group, fieldValue);
                }

            } catch (FieldNotFound e) {
                matches &= false;
            }
        }

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

    private boolean hasFieldValue(FieldMap fieldMap, FieldValue fieldValue) {
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
            throw new IllegalArgumentException(String.format("unable to process field %d with value type %s", fieldId, value.getClass()));
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

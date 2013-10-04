package com.qfu.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;

import java.math.BigDecimal;
import java.util.*;

import static java.lang.String.format;

/**
 * @author mtymes
 * @since 9/29/13 8:22 PM
 */
public class FIXMessageMatcher extends TypeSafeMatcher<Message> {

    private Class<? extends Message> messageType;
    private final List<FieldValue> headerFieldValues = new ArrayList<FieldValue>();
    private final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
    private final Map<GroupId, List<FieldValue>> groupFieldValues = new LinkedHashMap<GroupId, List<FieldValue>>();

    public static FIXMessageMatcher isFixMessage() {
        return new FIXMessageMatcher();
    }

    public static FIXMessageMatcher isFixMessage(Class<? extends Message> messageType) {
        return new FIXMessageMatcher().ofType(messageType);
    }

    public FIXMessageMatcher ofType(Class<? extends Message> messageType) {
        if (this.messageType != null) {
            throw new IllegalArgumentException(format("message type already defined as %s", this.messageType.getSimpleName()));
        }
        this.messageType = messageType;
        return this;
    }

    public FIXMessageMatcher withHeaderField(int fieldId, Object value) {
        headerFieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    public FIXMessageMatcher with(Header header) {
        headerFieldValues.addAll(header.getFieldValues());
        return this;
    }

    public FIXMessageMatcher with(int fieldId, Object value) {
        fieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    public FIXMessageMatcher withGroupField(int groupIndex, int groupTag, int fieldId, Object value) {
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

    public FIXMessageMatcher with(com.qfu.matcher.Group group) {
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

        if (messageType != null && !messageType.isAssignableFrom(message.getClass())) {
            matches = false;
        }

        for (FieldValue fieldValue : fieldValues) {
            if (!hasFieldValue(message, fieldValue)) {
                matches = false;
                break;
            }
        }

        if (matches) {
            Message.Header header = message.getHeader();
            for (FieldValue fieldValue : headerFieldValues) {
                if (!hasFieldValue(header, fieldValue)) {
                    matches = false;
                    break;
                }
            }
        }

        if (matches) {
            for (GroupId groupId : groupFieldValues.keySet()) {
                try {
                    Group group = message.getGroup(groupId.getIndex(), groupId.getGroupTag());

                    for (FieldValue fieldValue : groupFieldValues.get(groupId)) {
                        if (!hasFieldValue(group, fieldValue)) {
                            matches = false;
                            break;
                        }
                    }

                } catch (FieldNotFound e) {
                    matches = false;
                    break;
                }
            }
        }

        return matches;
    }

    // TODO: test this
    @Override
    public void describeTo(Description description) {
        description.appendText("a fix message");
        if (messageType != null) {
            description.appendText(format(" of Type '%s'", messageType.getSimpleName()));
        }
        if (!headerFieldValues.isEmpty()) {
            description.appendText(format(" with header values: %s", headerFieldValues));
        }
        if (!fieldValues.isEmpty()) {
            description.appendText(format(" with values: %s", fieldValues));
        }
        for (GroupId groupId : groupFieldValues.keySet()) {
            description.appendText(format(" with %d. group %d", groupId.getIndex(), groupId.getGroupTag()));
            List<FieldValue> groupValues = groupFieldValues.get(groupId);
            if (groupValues.isEmpty()) {
                description.appendText(" present");
            } else {
                description.appendText(format(" values: %s", groupValues));
            }
        }
    }

    // TODO: test this
    @Override
    public void describeMismatchSafely(Message message, Description description) {
        description.appendText("was a message");
        if (messageType != null) {
            description.appendText(format(" of Type '%s'", message.getClass().getSimpleName()));
        }
        if (!headerFieldValues.isEmpty()) {
            description.appendText(" with header values: ");
            describeActualValues(message.getHeader(), headerFieldValues, description);
        }
        if (!fieldValues.isEmpty()) {
            description.appendText(" with values: ");
            describeActualValues(message, fieldValues, description);
        }
        for (GroupId groupId : groupFieldValues.keySet()) {
            try {
                Group group = message.getGroup(groupId.getIndex(), groupId.getGroupTag());

                description.appendText(format(" with %d. group %d values: ", groupId.getIndex(), groupId.getGroupTag()));
                describeActualValues(group, groupFieldValues.get(groupId), description);

            } catch (FieldNotFound fieldNotFound) {
                description.appendText(format(" with %d. group %d missing", groupId.getIndex(), groupId.getGroupTag()));
            }
        }
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
            throw new IllegalArgumentException(format("unable to process field %d with value type %s", fieldId, value.getClass()));
        }
        return matches;
    }

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

    private void describeActualValues(FieldMap fieldMap, List<FieldValue> fieldValues, Description description) {
        description.appendText("[");

        boolean isFirst = true;
        for (FieldValue fieldValue : fieldValues) {
            if (isFirst) {
                isFirst = false;
            } else {
                description.appendText(", ");
            }

            int fieldId = fieldValue.getFieldId();
            try {
                description.appendText(FieldValue.toString(fieldId, fieldMap.getString(fieldId)));
            } catch (FieldNotFound e) {
                description.appendText(fieldId + " is undefined");
            }
        }
        description.appendText("]");
    }
}

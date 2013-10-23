package com.qfu.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import quickfix.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final FieldMatcher fieldMatcher;

    public FIXMessageMatcher() {
        this(FieldMatcher.INSTANCE);
    }

    FIXMessageMatcher(FieldMatcher fieldMatcher) {
        this.fieldMatcher = fieldMatcher;
    }

    public FIXMessageMatcher ofType(Class<? extends Message> messageType) {
        if (this.messageType != null) {
            throw new IllegalArgumentException(format("message type already defined as %s", this.messageType.getSimpleName()));
        }
        this.messageType = messageType;
        return this;
    }

    @Deprecated
    public FIXMessageMatcher withHeaderField(int fieldId, Object value) {
        headerFieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    public FIXMessageMatcher with(Header header) {
        headerFieldValues.addAll(header.getFieldValues());
        return this;
    }

    public FIXMessageMatcher with(Field field) {
        fieldValues.add(new FieldValue(field.getTag(), field.getObject()));
        return this;
    }

    public FIXMessageMatcher with(int fieldId, Object value) {
        fieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    @Deprecated
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
            if (!fieldMatcher.hasFieldValue(message, fieldValue)) {
                matches = false;
                break;
            }
        }

        if (matches) {
            Message.Header header = message.getHeader();
            for (FieldValue fieldValue : headerFieldValues) {
                if (!fieldMatcher.hasFieldValue(header, fieldValue)) {
                    matches = false;
                    break;
                }
            }
        }

        if (matches) {
            for (GroupId groupId : groupFieldValues.keySet()) {
                try {
                    quickfix.Group group = message.getGroup(groupId.getIndex(), groupId.getGroupTag());

                    for (FieldValue fieldValue : groupFieldValues.get(groupId)) {
                        if (!fieldMatcher.hasFieldValue(group, fieldValue)) {
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
                quickfix.Group group = message.getGroup(groupId.getIndex(), groupId.getGroupTag());

                description.appendText(format(" with %d. group %d values: ", groupId.getIndex(), groupId.getGroupTag()));
                describeActualValues(group, groupFieldValues.get(groupId), description);

            } catch (FieldNotFound fieldNotFound) {
                description.appendText(format(" with %d. group %d missing", groupId.getIndex(), groupId.getGroupTag()));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FIXMessageMatcher that = (FIXMessageMatcher) o;

        if (fieldValues != null ? !fieldValues.equals(that.fieldValues) : that.fieldValues != null) return false;
        if (groupFieldValues != null ? !groupFieldValues.equals(that.groupFieldValues) : that.groupFieldValues != null)
            return false;
        if (headerFieldValues != null ? !headerFieldValues.equals(that.headerFieldValues) : that.headerFieldValues != null)
            return false;
        if (messageType != null ? !messageType.equals(that.messageType) : that.messageType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = messageType != null ? messageType.hashCode() : 0;
        result = 31 * result + (headerFieldValues != null ? headerFieldValues.hashCode() : 0);
        result = 31 * result + (fieldValues != null ? fieldValues.hashCode() : 0);
        result = 31 * result + (groupFieldValues != null ? groupFieldValues.hashCode() : 0);
        return result;
    }

    /* ============================== */
    /* ---     helper methods     --- */
    /* ============================== */

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

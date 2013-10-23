package com.qfu.matcher;

import quickfix.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mtymes
 * @since 10/2/13 12:21 AM
 */
public class Group {

    private final GroupId groupId;
    private final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

    public Group(GroupId groupId) {
        this.groupId = groupId;
    }

    public static Group group(int groupIndex, int groupTag) {
        return new Group(new GroupId(groupIndex, groupTag));
    }

    public Group with(Field field) {
        fieldValues.add(new FieldValue(field.getTag(), field.getObject()));
        return this;
    }

    public Group with(int fieldId, Object value) {
        fieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    GroupId getGroupId() {
        return groupId;
    }

    List<FieldValue> getFieldValues() {
        return fieldValues;
    }
}

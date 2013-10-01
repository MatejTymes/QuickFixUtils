package com.matejtymes.qfu.matcher;

/**
 * @author mtymes
 * @since 10/1/13 2:53 PM
 */
// TODO: change into field matcher
class FieldValue {

    private final int fieldId;
    private final Object value;

    FieldValue(int fieldId, Object value) {
        this.fieldId = fieldId;
        this.value = value;
    }

    public int getFieldId() {
        return fieldId;
    }

    public Object getValue() {
        return value;
    }
}

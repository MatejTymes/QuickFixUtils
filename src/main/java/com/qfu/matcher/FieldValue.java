package com.qfu.matcher;

/**
 * @author mtymes
 * @since 10/1/13 2:53 PM
 */
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

    public String toString() {
        return toString(fieldId, value);
    }

    public static String toString(int fieldId, Object value) {
        return fieldId + " = " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldValue that = (FieldValue) o;

        if (fieldId != that.fieldId) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldId;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}

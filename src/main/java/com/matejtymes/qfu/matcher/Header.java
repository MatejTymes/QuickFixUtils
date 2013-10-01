package com.matejtymes.qfu.matcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mtymes
 * @since 10/2/13 12:21 AM
 */
// TODO: change into header matcher
public class Header {

    private final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

    public static Header header() {
        return new Header();
    }

    public Header with(int fieldId, Object value) {
        fieldValues.add(new FieldValue(fieldId, value));
        return this;
    }

    List<FieldValue> getFieldValues() {
        return fieldValues;
    }
}

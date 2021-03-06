package com.qfu.matcher;

import quickfix.Message;

/**
 * @author mtymes
 * @since 10/7/13 6:12 PM
 */
public class FIXMatchers {

    public static FIXMessageMatcher isFIXMessage() {
        return new FIXMessageMatcher();
    }

    public static FIXMessageMatcher isFIXMessage(Class<? extends Message> messageType) {
        return new FIXMessageMatcher().ofType(messageType);
    }
}

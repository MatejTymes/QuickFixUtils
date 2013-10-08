package com.qfu.matcher;

import org.junit.Test;
import quickfix.fix44.NewOrderSingle;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author mtymes
 * @since 10/7/13 8:16 PM
 */
public class FIXMatchersTest {

    @Test
    public void shouldCreateFIXMessageMatcher() {
        // When
        FIXMessageMatcher matcher = FIXMatchers.isFIXMessage();

        // Then
        assertThat(matcher.equals(new FIXMessageMatcher()), is(true));
    }

    @Test
    public void shouldCreateFIXMessageMatcherOfType() {
        // When
        FIXMessageMatcher matcher = FIXMatchers.isFIXMessage(NewOrderSingle.class);

        // Then
        assertThat(matcher.equals(new FIXMessageMatcher().ofType(NewOrderSingle.class)), is(true));
        assertThat(matcher.equals(new FIXMessageMatcher()), is(false));
    }
}

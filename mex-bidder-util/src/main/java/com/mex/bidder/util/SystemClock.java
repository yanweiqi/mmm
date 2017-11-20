package com.mex.bidder.util;

import org.joda.time.Instant;

/**
 * An implementation of {@link Clock} that returns the system time.
 */
public class SystemClock implements Clock {

    @Override
    public Instant now() {
        return new Instant();
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}

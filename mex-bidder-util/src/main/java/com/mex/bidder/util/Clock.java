package com.mex.bidder.util;

import com.google.inject.ImplementedBy;
import org.joda.time.Instant;


@ImplementedBy(SystemClock.class)
public interface Clock {

    /**
     * @return 返回当前日期/时间
     */
    Instant now();

    /**
     * @return 纳秒
     */
    long nanoTime();
}

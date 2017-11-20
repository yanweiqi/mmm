package com.mex.bidder.engine.logger;

import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class BidRequestLoggerTest {
    Logger bidRequestLogger = Logger.getLogger("BidRequestLog");
    @Test
    public void append() throws Exception {
        bidRequestLogger.info("hello world");
    }

    @Test
    public void append1() throws Exception {
        ImmutableList data = ImmutableList.of(1, 2, 3, 4, 5);
        bidRequestLogger.info(data);
    }

}
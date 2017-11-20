package com.mex.bidder.engine.logger;

import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class BidResponseLoggerTest {
    Logger bidResponseLogger = Logger.getLogger("BidResponseLog");

    @Test
    public void append() throws Exception {
        bidResponseLogger.info("BidResponseLoggerTest 112222");
    }

    @Test
    public void append1() throws Exception {
        ImmutableList data = ImmutableList.of("abc", "009", 3, "0999", 5);
        bidResponseLogger.info(data);
    }

}
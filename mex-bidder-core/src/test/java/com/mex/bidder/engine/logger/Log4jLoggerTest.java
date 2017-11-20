package com.mex.bidder.engine.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.openrtb.json.OpenRtbJsonExtWriter;
import com.google.openrtb.json.OpenRtbJsonFactory;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.mex.bidder.RtbDataTest;
import com.mex.bidder.api.openrtb.json.MexOpenRtbJsonFactory;
import com.mex.bidder.engine.logger.impl.Log4jLogger;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class Log4jLoggerTest {
    Log4jLogger log4jLogger;

    @Before
    public void setup() {
        Logger bidRequestLogger = Logger.getLogger("BidRequestLog");
        Logger bidResponseLogger = Logger.getLogger("BidResponseLog");

        log4jLogger = new Log4jLogger();
    }

    @Test
    public void append() throws Exception {
        OpenRtbJsonFactory factory = MexOpenRtbJsonFactory.create();
        OpenRtbJsonWriter openRtbJsonWriter = factory.newWriter();


        String bidRequestJson = openRtbJsonWriter.writeBidRequest(RtbDataTest.bidRequest);
        System.out.println(bidRequestJson);
    }

    class Test3Writer extends OpenRtbJsonExtWriter<Integer> {

        @Override
        protected void write(Integer ext, JsonGenerator gen) throws IOException {
            gen.writeNumberField("test3", ext);
        }
    }


}
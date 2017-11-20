package com.mex.bidder.api.openrtb.json;

import com.google.openrtb.json.OpenRtbJsonFactory;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.mex.bidder.RtbDataTest;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class MexOpenRtbJsonFactoryTest {
    OpenRtbJsonFactory factory = MexOpenRtbJsonFactory.create();
    OpenRtbJsonWriter openRtbJsonWriter = factory.newWriter();

    @Test
    public void newReader() throws Exception {

    }

    @Test
    public void newWriter() throws Exception {
        String s = openRtbJsonWriter.writeBidResponse(RtbDataTest.bidResponse);
        System.out.println(s);
    }


}
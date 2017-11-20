package com.mex.bidder.engine.filter.impl;

import com.google.openrtb.OpenRtb;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/11/17
 */
public class OsFitlerTest {
    @Test
    public void filter() throws Exception {
        OpenRtb.BidRequest bidRequest = OpenRtb.BidRequest.newBuilder().setId("hi").build();
        String os = bidRequest.getDevice().getOs();

        System.out.println(os);
    }

}
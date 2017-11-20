package com.mex.bidder.engine.filter.impl;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;

/**
 * xuchuanao
 * on 2017/3/10.
 */
public class ChannalDeviceIdFilter implements SimpleAdFilter {


    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        String id = bidRequest.getExchange().getId();
      /*  if (""){

        }*/

        return false;
    }
}

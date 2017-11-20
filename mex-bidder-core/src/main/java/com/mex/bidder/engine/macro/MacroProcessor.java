package com.mex.bidder.engine.macro;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;

/**
 * User: donghai
 * Date: 2016/11/21
 */
public interface MacroProcessor {
    public void process(BidRequest request, BidResponse response) throws Exception;
}

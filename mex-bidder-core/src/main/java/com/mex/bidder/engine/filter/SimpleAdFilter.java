package com.mex.bidder.engine.filter;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.protocol.Ad;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public interface SimpleAdFilter {

    /**
     * 返回 true 表示过滤，否则表示可以下发
     *
     * @param ad
     * @param bidRequest
     * @param bidResponse
     * @return
     */
    boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse);

}

package com.mex.bidder.engine.filter;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.protocol.Ad;
import rx.Observable;

import java.util.List;

/**
 * User: donghai
 * Date: 2016/11/27
 */
public interface AdListFilter {
    <B extends Ad> List<B> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse);

}

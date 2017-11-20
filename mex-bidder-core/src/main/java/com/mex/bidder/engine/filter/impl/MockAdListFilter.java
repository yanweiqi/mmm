package com.mex.bidder.engine.filter.impl;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.filter.AdListFilter;
import com.mex.bidder.protocol.Ad;

import java.util.List;

/**
 * user: donghai
 * date: 2017/1/4
 */
public class MockAdListFilter implements AdListFilter {
    @Override
    public <B extends Ad> List<B> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse) {
        return adList;
    }
}

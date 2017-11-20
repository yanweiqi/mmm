package com.mex.bidder.engine.filter.impl;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;

/**
 * 过滤链
 * <p>
 * User: donghai
 * Date: 2016/11/17
 */
public class CompositeAdFilter implements SimpleAdFilter {


    @Inject
    private Set<SimpleAdFilter> filterChain = Collections.emptySet();

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        for (SimpleAdFilter simpleAdFilter : filterChain) {
            if (simpleAdFilter.filter(ad, bidRequest, bidResponse)) {
                return true;
            }
        }
        return false;
    }
}

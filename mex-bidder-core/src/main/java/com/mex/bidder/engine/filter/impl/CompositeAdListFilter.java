package com.mex.bidder.engine.filter.impl;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.filter.AdListFilter;
import com.mex.bidder.protocol.Ad;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * xuchuahao
 * on 2017/7/7.
 */
public class CompositeAdListFilter implements AdListFilter {

    @Inject
    private Set<AdListFilter> filterChain = Collections.emptySet();

    @Override
    public <B extends Ad> List<B> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse) {

        for (AdListFilter adListFilter : filterChain) {
            adList = adListFilter.filter(adList, bidRequest, bidResponse);
        }
        return adList;
    }
}

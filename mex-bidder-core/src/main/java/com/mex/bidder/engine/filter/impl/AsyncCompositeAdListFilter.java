package com.mex.bidder.engine.filter.impl;

import com.google.api.client.util.Lists;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.filter.AsyncAdListFilter;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * user: donghai
 * date: 2017/6/16
 */
public class AsyncCompositeAdListFilter implements AsyncAdListFilter {
    private static final Logger logger = LoggerFactory.getLogger(AsyncCompositeAdListFilter.class);
    private List<AsyncAdListFilter> filterChain = Collections.emptyList();

    @Inject
    public AsyncCompositeAdListFilter(Set<AsyncAdListFilter> filterChain) {
        this.filterChain = Lists.newArrayList(filterChain);
    }

    @Override
    public <B extends Ad> Future<List<B>> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse) {

        Future<List<B>> result = Future.future();

        if (filterChain.isEmpty()) {
            result.complete(adList);
            return result;
        }

        Future<List<B>> compositeFuture = filterChain.get(0).filter(adList, bidRequest, bidResponse);
        for (int i = 0; i < filterChain.size(); i++) {
            if (i == 0) continue;
            AsyncAdListFilter filter = filterChain.get(i);
            compositeFuture = compositeFuture.compose(rs -> filter.filter(rs, bidRequest, bidResponse));
        }
        compositeFuture.setHandler(rs -> {
            if (rs.succeeded()) {
                result.complete(rs.result());
            } else {
                logger.error("async filter error.", rs.cause());
                result.fail(rs.cause());
            }
        });

        return result;
    }

}

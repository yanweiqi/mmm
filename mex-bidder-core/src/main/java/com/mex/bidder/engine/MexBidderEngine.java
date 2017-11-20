package com.mex.bidder.engine;

import com.google.common.collect.Lists;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.filter.AdListFilter;
import com.mex.bidder.engine.filter.impl.AsyncCompositeAdListFilter;
import com.mex.bidder.engine.filter.impl.CompositeAdFilter;
import com.mex.bidder.engine.filter.impl.CompositeAdListFilter;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.ranking.AdRanking;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * 竞价引擎
 * User: donghai
 * Date: 2016/11/19
 */
public class MexBidderEngine {
    private static final Logger logger = LoggerFactory.getLogger(MexBidderEngine.class);

    @Inject
    private CompositeAdFilter simpleAdFilter;

    @Inject
    private AsyncCompositeAdListFilter asyncAdListFilter;

    @Inject
    private CompositeAdListFilter adListFilter;

    @Inject
    private AdRanking adRanking;

    public <T extends Ad> Future<AdAndPricePair> bidding(List<T> candidatesAdList, BidRequest bidRequest, BidResponse bidResponse) {
        // 按targeting 过滤
        List<T> filteredAdList = filter(candidatesAdList, bidRequest, bidResponse);
        filteredAdList = adListFilter.filter(filteredAdList, bidRequest, bidResponse);

        return asyncAdListFilter.filter(filteredAdList, bidRequest, bidResponse).compose(dmpFilterAdList -> {
            Future<AdAndPricePair> result = Future.future();
//            // 广告排序后计算最优广告
            AdAndPricePair adAndPricePair = adRanking.process(dmpFilterAdList, bidRequest, bidResponse);

            result.complete(adAndPricePair);
            return result;
        });
    }


    /**
     * targeting广告  过滤链
     *
     * @param candidatesAdList 渠道下的所有的广告
     * @param bidRequest       请求对象
     * @return
     */
    private <B extends Ad> List<B> filter(List<B> candidatesAdList, BidRequest bidRequest, BidResponse bidResponse) {
        List<B> selectedAdList = Lists.newArrayList();

        candidatesAdList.forEach(ad -> {
            if (!simpleAdFilter.filter(ad, bidRequest, bidResponse)) {
                selectedAdList.add(ad);
            }
        });

        return selectedAdList;
    }
}

package com.mex.bidder.engine.ranking;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.Banner;

import java.util.List;

/**
 * 广告排序算法
 * <p>
 * User: donghai
 * Date: 2016/11/16
 */
public interface AdRanking {
    /**
     * 广告排序算法
     *
     * @param candidates 可下发广告集
     * @return 最优广告与对应出价
     */
    public  <T extends Ad> AdAndPricePair process(List<T> candidates, BidRequest bidRequest, BidResponse bidResponse);
}

package com.mex.bidder.engine.ranking.impl;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.ranking.AdRanking;
import com.mex.bidder.engine.util.BidPrice;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.AdxDict;
import com.mex.bidder.protocol.Banner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * 随机算法
 * <p>
 * User: donghai
 * Date: 2016/11/16
 */
public class RandomAdRanking implements AdRanking {

    public static final String NAME = "random";

    private final Random random = new Random();

    @Inject
    private MexDataContext mexDataContext;


    @Override
    public  <T extends Ad>  AdAndPricePair process(List<T> candidates, BidRequest bidRequest, BidResponse bidResponse) {
        Ad ad;
        if (candidates == null || candidates.isEmpty()) {
            ad = Ad.NULL;
        } else if (candidates.size() == 1) {
            ad = candidates.get(0);
        } else {
            ad = candidates.get(random.nextInt(candidates.size()));
        }

        AdAndPricePair result;
        if (ad == Ad.NULL) {
            result = AdAndPricePair.EMPTY;
        } else {
            // 查询CTR
//            Map<String, String> ctrDictMap = mexDataContext.getAdxDictMap(
//                    bidRequest.getExchange(), ChannelDictType.ctr);
//            double price = BidPrice.calcPrice(ad, ctrDictMap);

            AdxDict adxDict = mexDataContext.getAdxDictByExchange(bidRequest.getExchange());
//            ad.setAdxBidPrice(new BigDecimal(11));
            BigDecimal price = BidPrice.calcPrice(ad, adxDict);
            //  TODO  临时设置

            result = AdAndPricePair.create(ad, price);
        }
        return result;
    }


}

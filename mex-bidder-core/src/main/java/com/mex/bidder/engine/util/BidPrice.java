package com.mex.bidder.engine.util;

import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.AdxDict;

import java.math.BigDecimal;

/**
 * User: donghai
 * Date: 2016/12/4
 */
public class BidPrice {

    /**
     * TODO 出价应该合并到Ranking中, 以后按ecpm排序。 ecpm = bidding price * CTR
     * double price = calcPrice(ad);
     *
     * @param ad
     * @return
     */
    public static BigDecimal calcPrice(Ad ad, AdxDict adxDict) {

        return  ad.getAdxBidPrice();
    }
}

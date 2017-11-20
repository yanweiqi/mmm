package com.mex.bidder.engine.model;

import com.mex.bidder.protocol.Ad;

import java.math.BigDecimal;

/**
 * 广告素材与对应出价对
 * <p>
 * User: donghai
 * Date: 2016/11/19
 */
public class AdAndPricePair {
    public static final AdAndPricePair EMPTY = create(Ad.NULL, BigDecimal.valueOf(0));
    
    private  Ad ad = Ad.NULL;

    private BigDecimal price = BigDecimal.ZERO;

    public static AdAndPricePair create(Ad ad, BigDecimal price) {
        return new AdAndPricePair(ad, price);
    }

    protected AdAndPricePair(Ad ad, BigDecimal price) {
        this.ad = ad;
        this.price = price;
    }

    public <T> T getAd() {
        return (T)ad;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

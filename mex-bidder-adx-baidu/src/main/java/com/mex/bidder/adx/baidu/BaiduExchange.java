package com.mex.bidder.adx.baidu;

import com.mex.bidder.api.platform.Exchange;

/**
 * User: donghai
 * Date: 2016/11/15
 */
public class BaiduExchange extends Exchange {
    public static final String ID = "adsbd";
    public static final BaiduExchange INSTANCE = new BaiduExchange(ID);

    protected BaiduExchange(String id) {
        super(ID);
    }

    @Override
    public Object newNativeResponse() {
        return null;
    }
}

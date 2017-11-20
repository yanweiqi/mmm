package com.mex.bidder.adx.sohu;

import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.Constants;

/**
 * xuchuahao
 * on 2017/3/20.
 */
public class SohuExchange extends Exchange {

    public static  final String ID = Constants.SOHU_ID;
    public static final SohuExchange INSTANCE = new SohuExchange(ID);

    protected SohuExchange(String id) {
        super(id);
    }

    @Override
    public Object newNativeResponse() {
        return null;
    }
}

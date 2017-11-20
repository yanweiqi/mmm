package com.mex.bidder.adx.meitu;

import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.Constants;

/**
 * xuchuahao
 * on 2017/6/12.
 */
public class MeituExchange extends Exchange {
    public static final String ID = Constants.MEITU_ID;
    public static final Exchange INSTANCE = new MeituExchange(ID);


    protected MeituExchange(String id) {
        super(id);
    }

    @Override
    public Object newNativeResponse() {
        return null;
    }
}

package com.mex.bidder.adx.zplay;

import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.Constants;

/**
 * User: donghai
 * Date: 2016/11/15
 */
public class ZplayExchange extends Exchange {
    public static final String ID = Constants.ZPLAY_ID;
    public static final ZplayExchange INSTANCE = new ZplayExchange(ID);

    protected ZplayExchange(String id) {
        super(ID);
    }

    @Override
    public Object newNativeResponse() {
        return null;
    }
}

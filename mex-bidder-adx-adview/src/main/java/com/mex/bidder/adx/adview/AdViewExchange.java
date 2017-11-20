package com.mex.bidder.adx.adview;

import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.Constants;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class AdViewExchange extends Exchange {
    public static final String ID = Constants.ADVIEW_ID;
    public static final Exchange INSTANCE = new AdViewExchange(ID);

    protected AdViewExchange(String id) {
        super(id);
    }

    @Override
    public Object newNativeResponse() {
        return null;
    }
}

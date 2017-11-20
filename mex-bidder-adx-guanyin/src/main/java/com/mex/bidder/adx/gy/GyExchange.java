package com.mex.bidder.adx.gy;

import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.Constants;

/**
 * User: donghai
 * Date: 2016/11/15
 */
public class GyExchange extends Exchange {
    public static final String ID = Constants.GY_ID;
    public static final GyExchange INSTANCE = new GyExchange(ID);

    protected GyExchange(String id) {
        super(ID);
    }

    @Override
    public Object newNativeResponse() {
        return null;
    }
}

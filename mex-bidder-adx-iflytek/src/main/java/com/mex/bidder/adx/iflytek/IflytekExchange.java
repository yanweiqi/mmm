package com.mex.bidder.adx.iflytek;

import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.Constants;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class IflytekExchange extends Exchange {
    public static final String ID = Constants.IFLYTEK_ID;
    public static final Exchange INSTANCE = new IflytekExchange(ID);

    protected IflytekExchange(String id) {
        super(id);
    }

    @Override
    public Object newNativeResponse() {
        return null;
    }
}

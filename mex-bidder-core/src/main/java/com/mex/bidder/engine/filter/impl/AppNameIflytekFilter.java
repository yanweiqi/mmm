package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xuchuahao
 * on 2017/3/21.
 */
public class AppNameIflytekFilter implements SimpleAdFilter {
    private static final Logger logger = LoggerFactory.getLogger(AppNameIflytekFilter.class);

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        OpenRtb.BidRequest.Builder request = bidRequest.openRtb().toBuilder();
        String id = bidRequest.getExchange().getId();

        if (Constants.IFLYTEK_ID.equals(id)) {

            String bundle = request.getApp().getBundle();
            if (Strings.isNullOrEmpty(bundle)) {
                logger.info("iflytek appname filter,bundle is null");
                return true;
            } else if ("com.jxedt".equals(bundle)) {
                logger.info("iflytek appname pass,id="+id+", bundle="+bundle);
                return false;
            } else {
                logger.info("iflytek appname filter,id="+id+", bundle="+bundle);
                return true;
            }
        } else {
            logger.info("iflytek appname pass,id="+id);
            return false;
        }
    }
}

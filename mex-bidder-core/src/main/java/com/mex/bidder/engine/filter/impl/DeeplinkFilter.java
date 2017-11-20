package com.mex.bidder.engine.filter.impl;

import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.constants.OS;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.protocol.Ad;

/**
 * xuchuahao
 * on 2017/6/5.
 */
public class DeeplinkFilter implements SimpleAdFilter {

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        Exchange exchange = bidRequest.getExchange();
        //iflytek
        if (Constants.IFLYTEK_ID.equals(exchange.getId())) {
            if (!bidRequest.openRtb().getImp(0).getExtension(MexOpenRtbExt.isSupportDeeplink)) {
                if (ad.hasDeeplink()) {
                    bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_DEEPLINK);
                    return true;
                }
            }
        }

        //zplay 只能投ios
        if (Constants.ZPLAY_ID.equals(exchange.getId())) {
            OS os = MexUtil.toMexOS(bidRequest, mexDataContext);
            if (ad.hasDeeplink()) {
                if (os.equals(OS.Android)) {
                    bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_DEEPLINK);
                    return true;
                }
            }
        }

        return false;
    }
}

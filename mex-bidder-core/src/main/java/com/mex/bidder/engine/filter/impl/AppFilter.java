package com.mex.bidder.engine.filter.impl;

import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.constants.OS;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.AppTargetingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xuchuanao
 * on 2017/3/7.
 */
public class AppFilter implements SimpleAdFilter {

    private static final Logger logger = LoggerFactory.getLogger(AppFilter.class);

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        String id = bidRequest.getExchange().getId();
        OpenRtb.BidRequest request = bidRequest.openRtb();

        String bundle = "";
        if (request.hasApp() && request.getApp().hasBundle()) {
            bundle = request.getApp().getBundle();
        }
        AppTargetingData appTargetingData = mexDataContext.getAppTargetingData();
        OS os = MexUtil.toMexOS(bidRequest, mexDataContext);

        boolean is = appTargetingData.isFilter(bundle, ad, os);

        logger.info("channel=" + id + ", groupid=" + ad.getAdGroupId() + ", bundle=" + bundle);
        if (is) {
            //
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_APP_NAME);
            return true;
        }
        //
        return false;
    }

}

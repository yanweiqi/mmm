package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xuchuahao
 * on 2017/4/1.
 */
public class CarrierFilter implements SimpleAdFilter {

    private static final Logger logger = LoggerFactory.getLogger(CarrierFilter.class);


    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        if (Strings.isNullOrEmpty(ad.getTargetTeleOper())) {
            // 空串 代表不限
            logger.info("carrier pass, ad carrier is null, groupid=" + ad.getAdGroupId());
            return false;
        }
        String carrier = bidRequest.openRtb().getDevice().getCarrier();

        if (ad.getTargetTeleOper().contains(carrier)) {
            logger.info("carrier pass, reqid=" + bidRequest.openRtb().getId() + ", ad carrier=" + ad.getTargetTeleOper() + ", request carrier=" + bidRequest.openRtb().getDevice().getCarrier());
            return false;
        } else {
            logger.info("carrier filter, reqid=" + bidRequest.openRtb().getId() + ", ad carrier=" + ad.getTargetTeleOper() + ", request carrier=" + bidRequest.openRtb().getDevice().getCarrier());

            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_CARRIER);
            return true;
        }
    }
}

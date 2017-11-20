package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.constants.OS;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.protocol.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * OS 定向过滤
 * User: donghai
 * Date: 2016/11/17
 */
public class OsFitler implements SimpleAdFilter {
    private static final Logger logger = LoggerFactory.getLogger(OsFitler.class);

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        // 无定向
        String target_os = ad.getOsType();
        if (Strings.isNullOrEmpty(target_os)) {
            return false;
        }

        OS os = MexUtil.toMexOS(bidRequest, mexDataContext);

        logger.info("requestid="+bidRequest.openRtb().getId()+", adgroupid="+ad.getAdGroupId()+", target_os="+target_os+", channel_os="+os.getCode());

        if (target_os.contains(os.getCode())) {
            return false;
        } else {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_OS);
            return true;
        }

    }
}

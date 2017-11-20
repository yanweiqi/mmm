package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * User: donghai
 * Date: 2016/11/17
 */
public class OsVersionFitler implements SimpleAdFilter {
    private static final Logger logger = LoggerFactory.getLogger(OsVersionFitler.class);

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        // 无OS版本定向，跳过

        if (!ad.isOsVersionCtrl()) {
            return false;
        }


        String osv = getOsv(bidRequest);
        // 如果当前请求os版本为空，并且投放设置了版本定向，直接过滤
        if (Strings.isNullOrEmpty(osv)) {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_Version);
            return true;
        }

        String[] versions = osv.split("\\.");

        if (versions.length <= 1) {
            logger.warn("illegal os version {}", osv);
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_Version);
            return true;
        }
        int isv = Integer.parseInt(versions[0])*10+Integer.parseInt(versions[1]);

        int os_version_max = getOsVersionMax(ad);
        int os_version_min = getOsVersionMin(ad);

        if (os_version_min < isv && isv > os_version_max) {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_Version);
            return true;
        } else {
            return false;
        }
    }

    int getOsVersionMin(Ad ad) {
        int os_version_min = ad.getOsMinVersion();
        if (0==os_version_min) {
            return Integer.MIN_VALUE;
        } else {
            return os_version_min;
        }
    }

    int getOsVersionMax(Ad ad) {
        int os_version_max = ad.getOsMaxVersion();
        if (0==os_version_max) {
            return Integer.MAX_VALUE;
        } else {
            return os_version_max;
        }
    }

    private static String getOsv(BidRequest bidRequest) {
        if (bidRequest.openRtb().hasDevice()) {
            return bidRequest.openRtb().getDevice().getOsv();
        } else {
            return null;
        }
    }
}

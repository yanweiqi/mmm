package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;

/**
 * 当前网络类型定向
 * <p>
 * User: donghai
 * Date: 2016/11/17
 */
public class NetworkTypeFilter implements SimpleAdFilter {
    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        String connect_type = ad.getConnectType();
        // 无定向
        if (Strings.isNullOrEmpty(connect_type)) {
            return false;
        }

        String target = getNetworkType(bidRequest);
        // 请求类型为空，或定向中不包括请求的类型，记录错误信息
        if (Strings.isNullOrEmpty(target) || !connect_type.contains(target)) {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_ConType);
            return true;
        } else {
            return false;
        }
    }

    private static String getNetworkType(BidRequest bidRequest) {
        String type = "";
        if (bidRequest.openRtb().hasDevice()) {
            OpenRtb.ConnectionType connectiontype = bidRequest.openRtb().getDevice().getConnectiontype();
            type = String.valueOf(connectiontype.getNumber());
        }

        return type;
    }
}

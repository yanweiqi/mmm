package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.engine.model.IpBean;

/**
 * 地域定向
 * User: donghai
 * Date: 2016/11/17
 */
public class GeoCityFilter implements SimpleAdFilter {

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        IpBean ipBean = bidRequest.getIpBean();

        String mvgeoids = ad.getCity();

        // 无地域定向，不过滤
        if (Strings.isNullOrEmpty(mvgeoids)) {
            return false;
        } else {

            // 当设置地域定向，并且IP没解出来时，记录为缺少ua或ip
            if (ipBean == IpBean.NULL) {
                bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_NoUaIp);
                return true;
            } else {
                // 如果地域ip列表中不包括ip所在的城市就从列表中移除
                if (!mvgeoids.contains(String.valueOf(ipBean.getCityId()))) {
                    bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_CityHunt);
                    return true;
                }
                return false;
            }

        }
    }
}

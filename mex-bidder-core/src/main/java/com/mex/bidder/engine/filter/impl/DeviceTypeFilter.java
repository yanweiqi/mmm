package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.constants.DeviceType;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;

/**
 * 设备类型定向
 * <p>
 * User: donghai
 * Date: 2016/11/17
 */
public class DeviceTypeFilter implements SimpleAdFilter {

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        String device_type = ad.getDeviceType();
        // 无设备类型定向
        if (Strings.isNullOrEmpty(device_type)) {
            return false;
        }

        DeviceType currentDeviceType = getType(bidRequest);

        // 无关联
        if (currentDeviceType == null) {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_DevType);
            return true;
        }
        // 包括
        else if (device_type.contains(currentDeviceType.value())) {
            return false;
        } else {
            // 不匹配
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_DevType);
            return true;
        }
    }

    private static DeviceType getType(BidRequest bidRequest) {
        if (bidRequest.openRtb().hasDevice()) {
            OpenRtb.BidRequest.Device device = bidRequest.openRtb().getDevice();
            // 请求有设置类型
            if (device.hasDevicetype()) {
                return DeviceType.lookup(device.getDevicetype());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

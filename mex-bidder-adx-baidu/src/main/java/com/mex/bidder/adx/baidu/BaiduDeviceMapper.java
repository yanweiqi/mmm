package com.mex.bidder.adx.baidu;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.dict.DictMapper;
import com.mex.bidder.engine.constants.DeviceType;

/**
 * 将Baidu的设备转成Mex
 * <p>
 * Baidu设备类型，0：未知，1：手机， 2：平板
 * User: donghai
 * Date: 2016/11/21
 */
public class BaiduDeviceMapper implements DictMapper<BaiduRtb.BidRequest.Mobile.MobileDeviceType, OpenRtb.DeviceType> {

    @Override
    public OpenRtb.DeviceType toMex(BaiduRtb.BidRequest.Mobile.MobileDeviceType input) {

        int number = input.getNumber();
        OpenRtb.DeviceType value;
        switch (number) {
            case 1:
                value = DeviceType.HIGHEND_PHONE.openRtbType;
                break;
            case 2:
                value = DeviceType.TABLET.openRtbType;
                break;
            default:
                value = null;
                break;
        }
        return value;
    }
}

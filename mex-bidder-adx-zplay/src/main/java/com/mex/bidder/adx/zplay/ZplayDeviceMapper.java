package com.mex.bidder.adx.zplay;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.dict.DictMapper;
import com.mex.bidder.engine.constants.DeviceType;

/**
 * 将zplay的设备转成Mex
 * <p>
 * Zplay设备类型，1：移动设备，4：手机， 5：平板
 * User: donghai
 * Date: 2016/11/21
 */
public class ZplayDeviceMapper implements DictMapper<Integer, OpenRtb.DeviceType> {

    @Override
    public OpenRtb.DeviceType toMex(Integer input) {
        if (input == null) {
            return null;
        }
        OpenRtb.DeviceType value;
        switch (input) {
            case 1:
                value = DeviceType.HIGHEND_PHONE.openRtbType;
                break;
            case 4:
                value = DeviceType.HIGHEND_PHONE.openRtbType;
                break;
            case 5:
                value = DeviceType.TABLET.openRtbType;
                break;
            default:
                value = null;
                break;
        }
        return value;
    }
}

package com.mex.bidder.adx.gy;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.dict.DictMapper;
import com.mex.bidder.engine.constants.DeviceType;

/**
 * 将gy的设备转成Mex
 * <p>
 * Gy设备类型，1：phone 2.pad
 * User: donghai
 * Date: 2016/11/21
 */
public class GyDeviceMapper implements DictMapper<Integer, OpenRtb.DeviceType> {

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
            default:
                value = null;
                break;
        }
        return value;
    }
}

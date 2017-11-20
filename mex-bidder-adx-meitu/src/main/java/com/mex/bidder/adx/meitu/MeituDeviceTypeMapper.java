package com.mex.bidder.adx.meitu;

import com.google.openrtb.OpenRtb;
import com.meitu.openrtb.MeituOpenRtb;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/6/14.
 */
public class MeituDeviceTypeMapper implements DictMapper<MeituOpenRtb.DeviceType, OpenRtb.DeviceType> {

    public static MeituDeviceTypeMapper mapper = new MeituDeviceTypeMapper();

    @Override
    public OpenRtb.DeviceType toMex(MeituOpenRtb.DeviceType input) {
        switch (input) {
            case MOBILE:
                return OpenRtb.DeviceType.HIGHEND_PHONE;
            case HIGHEND_PHONE:
                return OpenRtb.DeviceType.HIGHEND_PHONE;
            case TABLET:
                return OpenRtb.DeviceType.TABLET;
            default:
                return OpenRtb.DeviceType.HIGHEND_PHONE;
        }
    }
}

package com.mex.bidder.adx.iflytek;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.constants.DeviceType;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuanao
 * on 2017/1/3.
 */
public class IflytekDeviceTypeMapper implements DictMapper<Integer,OpenRtb.DeviceType> {

    public static final IflytekDeviceTypeMapper mapper = new IflytekDeviceTypeMapper();

    @Override
    public OpenRtb.DeviceType toMex(Integer input) {
        if(input == null){
            return null;
        }
        OpenRtb.DeviceType value;

        switch (input) {
            case 0:
                value = DeviceType.HIGHEND_PHONE.openRtbType;
                break;
            case 1:
                value = DeviceType.TABLET.openRtbType;
                break;
            default:
                value = null;
                break;
        }
        return value;
    }
}

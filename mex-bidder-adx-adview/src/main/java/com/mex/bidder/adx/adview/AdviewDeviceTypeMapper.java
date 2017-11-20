package com.mex.bidder.adx.adview;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.constants.DeviceType;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * Created by Administrator on 2016/12/18.
 */
public class AdviewDeviceTypeMapper  implements DictMapper<Integer,OpenRtb.DeviceType> {
    public static final AdviewDeviceTypeMapper mapper = new AdviewDeviceTypeMapper();
    @Override
    public OpenRtb.DeviceType toMex(Integer input) {
        if(input == null){
            return null;
        }

        OpenRtb.DeviceType value;

        switch (input) {
            case 1:
                value = DeviceType.HIGHEND_PHONE.openRtbType;
                break;
            case 2:
                value = DeviceType.HIGHEND_PHONE.openRtbType;
                break;
            case 3:
                value = DeviceType.TABLET.openRtbType;
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

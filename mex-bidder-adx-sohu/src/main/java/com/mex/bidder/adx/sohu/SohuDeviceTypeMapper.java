package com.mex.bidder.adx.sohu;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.constants.DeviceType;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/3/23.
 */
public class SohuDeviceTypeMapper implements DictMapper<Integer, OpenRtb.DeviceType> {

    public static final SohuDeviceTypeMapper mapper = new SohuDeviceTypeMapper();

    @Override
    public OpenRtb.DeviceType toMex(Integer input) {
        if (null == input) {
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

    public enum SohuDeviceDic {
        MOBILE("mobile", 0), //
        PC("pc", 2), //
        WAP("wap", 3),; //

        public String type;
        public int val;

        SohuDeviceDic(String type, int val) {
            this.type = type;
            this.val = val;
        }
    }
}

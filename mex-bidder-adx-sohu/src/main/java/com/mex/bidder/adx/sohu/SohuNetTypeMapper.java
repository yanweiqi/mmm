package com.mex.bidder.adx.sohu;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.constants.ConnectionType;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/3/23.
 */
public class SohuNetTypeMapper implements DictMapper<String, OpenRtb.ConnectionType> {

    public static final SohuNetTypeMapper mapper = new SohuNetTypeMapper();

    @Override
    public OpenRtb.ConnectionType toMex(String input) {
        if (input == null) {
            return ConnectionType.OTHER.openRtbType;
        }
        OpenRtb.ConnectionType value;
        switch (input) {
            case "2G":
                value = ConnectionType.CELL_2G.openRtbType;
                break;
            case "3G":
                value = ConnectionType.CELL_3G.openRtbType;
                break;
            case "4G":
                value = ConnectionType.CELL_4G.openRtbType;
                break;
            case "WIFI":
                value = ConnectionType.WIFI.openRtbType;
                break;
            default:
                value = ConnectionType.OTHER.openRtbType;
                break;
        }
        return value;
    }
}

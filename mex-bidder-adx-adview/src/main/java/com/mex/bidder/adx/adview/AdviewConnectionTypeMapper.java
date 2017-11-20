package com.mex.bidder.adx.adview;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.constants.ConnectionType;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * Created by Administrator on 2016/12/18.
 */
public class AdviewConnectionTypeMapper implements DictMapper<Integer,OpenRtb.ConnectionType> {

    public static final AdviewConnectionTypeMapper mapper = new AdviewConnectionTypeMapper();

    @Override
    public  OpenRtb.ConnectionType toMex(Integer input) {
        if (input == null){
            return ConnectionType.OTHER.openRtbType;
        }

        OpenRtb.ConnectionType value;
        switch (input){
            case 0:
                value = ConnectionType.OTHER.openRtbType;
                break;
            case 1:
                value = ConnectionType.ETHERNET.openRtbType;
                break;
            case 2:
                value = ConnectionType.WIFI.openRtbType;
                break;
            case 3:
                value = ConnectionType.UNKNOW.openRtbType;
                break;
            case 4:
                value = ConnectionType.CELL_2G.openRtbType;
                break;
            case 5:
                value = ConnectionType.CELL_3G.openRtbType;
                break;
            case 6:
                value = ConnectionType.CELL_4G.openRtbType;
                break;
            default:
                value = ConnectionType.OTHER.openRtbType;
                break;
        }

        return value;
    }
}

package com.mex.bidder.adx.baidu;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.dict.DictMapper;
import com.mex.bidder.engine.constants.ConnectionType;

import javax.inject.Singleton;

/**
 * 将Baidu的设备转成Mex
 * <p>
 * Baidu 网络连接类型 字典，
 * <p>
 * UNKNOWN_NETWORK = 0;   WIFI = 1;   MOBILE_2G = 2;   MOBILE_3G = 3;   MOBILE_4G = 4
 * User: donghai
 * Date: 2016/11/21
 */
@Singleton
public class BaiduConnectionTypeMapper implements DictMapper<Integer, OpenRtb.ConnectionType> {

    @Override
    public OpenRtb.ConnectionType toMex(Integer input) {
        if (input == null) {
            return ConnectionType.OTHER.openRtbType;
        }
        OpenRtb.ConnectionType value;
        switch (input) {
            case 0:
                value = ConnectionType.UNKNOW.openRtbType;
                break;
            case 1:
                value = ConnectionType.WIFI.openRtbType;
                break;
            case 2:
                value = ConnectionType.CELL_2G.openRtbType;
                break;
            case 3:
                value = ConnectionType.CELL_3G.openRtbType;
                break;
            case 4:
                value = ConnectionType.CELL_4G.openRtbType;
                break;
            default:
                value = ConnectionType.OTHER.openRtbType;
                break;
        }
        return value;
    }
}

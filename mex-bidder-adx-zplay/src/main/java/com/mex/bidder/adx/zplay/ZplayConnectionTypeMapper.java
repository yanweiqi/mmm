package com.mex.bidder.adx.zplay;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.engine.dict.DictMapper;
import com.mex.bidder.engine.constants.ConnectionType;

import javax.inject.Singleton;

/**
 * 将zplay的设备转成Mex
 * <p>
 * zplay 网络连接类型 字典，
 * <p>
 * 0：未知，1：以太网，2： wifi， 3：位置蜂窝网络，
 * 4：2G 网络，5： 3G 网络，6：4G 网络，详见 proto 文件
 * User: donghai
 * Date: 2016/11/21
 */
@Singleton
public class ZplayConnectionTypeMapper implements DictMapper<Integer, OpenRtb.ConnectionType> {

    @Override
    public OpenRtb.ConnectionType toMex(Integer input) {
        if (input == null) {
            return ConnectionType.OTHER.openRtbType;
        }
        OpenRtb.ConnectionType value;
        switch (input) {
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

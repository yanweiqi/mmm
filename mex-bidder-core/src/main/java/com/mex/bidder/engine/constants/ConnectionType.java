package com.mex.bidder.engine.constants;

import com.google.common.collect.ImmutableMap;
import com.google.openrtb.OpenRtb;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络类型 定向
 * User: donghai
 * Date: 2016/11/21
 */
public enum ConnectionType {
    OTHER(0, OpenRtb.ConnectionType.CONNECTION_UNKNOWN),
    ETHERNET(1, OpenRtb.ConnectionType.ETHERNET),
    WIFI(2, OpenRtb.ConnectionType.WIFI),
    UNKNOW(3, OpenRtb.ConnectionType.CELL_UNKNOWN),
    CELL_2G(4, OpenRtb.ConnectionType.CELL_2G),
    CELL_3G(5, OpenRtb.ConnectionType.CELL_3G),
    CELL_4G(6, OpenRtb.ConnectionType.CELL_4G),;

    public int value;
    public OpenRtb.ConnectionType openRtbType;

    ConnectionType(int value, OpenRtb.ConnectionType openRtbType) {
        this.value = value;
        this.openRtbType = openRtbType;
    }

    public static final ImmutableMap<Integer, ConnectionType> lookupTable;

    static {
        Map<Integer, ConnectionType> tmpMap = new HashMap<>();
        for (ConnectionType connectType : ConnectionType.values()) {
            tmpMap.put(connectType.value, connectType);
        }
        lookupTable = ImmutableMap.copyOf(tmpMap);
    }

    public ConnectionType lookup(Integer key) {
        if (lookupTable.containsKey(key)) {
            return lookupTable.get(key);
        } else {
            return OTHER;
        }
    }


}

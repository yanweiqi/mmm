package com.mex.bidder.engine.constants;

import com.google.common.collect.ImmutableMap;
import com.google.openrtb.OpenRtb;

import java.util.HashMap;
import java.util.Map;

/**
 * User: donghai
 * Date: 2016/11/21
 */
public enum DeviceType {
      // 智能手机
    HIGHEND_PHONE("3", OpenRtb.DeviceType.HIGHEND_PHONE),
    // 移动平板
    TABLET("4", OpenRtb.DeviceType.TABLET);

    public String value;
    public OpenRtb.DeviceType openRtbType;

    DeviceType(String value, OpenRtb.DeviceType openRtbType) {
        this.value = value;
        this.openRtbType = openRtbType;
    }

    private static final ImmutableMap<Integer, DeviceType> lookupTable;

    static {
        Map<Integer, DeviceType> tmpMap = new HashMap<>();
        for (DeviceType type : DeviceType.values()) {
            tmpMap.put(type.openRtbType.getNumber(), type);
        }
        lookupTable = ImmutableMap.copyOf(tmpMap);
    }

    public static DeviceType lookup(OpenRtb.DeviceType key) {
        if (lookupTable.containsKey(key.getNumber())) {
            return lookupTable.get(key.getNumber());
        } else {
            return null;
        }
    }

    public String value() {
        return this.value;
    }
}

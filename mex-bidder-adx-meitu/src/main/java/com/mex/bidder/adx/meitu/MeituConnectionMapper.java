package com.mex.bidder.adx.meitu;

import com.google.openrtb.OpenRtb;
import com.meitu.openrtb.MeituOpenRtb;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/6/14.
 */
public class MeituConnectionMapper implements DictMapper<MeituOpenRtb.ConnectionType, OpenRtb.ConnectionType> {

    public static MeituConnectionMapper mapper = new MeituConnectionMapper();

    @Override
    public OpenRtb.ConnectionType toMex(MeituOpenRtb.ConnectionType input) {
        switch (input) {
            case CONNECTION_UNKNOWN:
                return OpenRtb.ConnectionType.CONNECTION_UNKNOWN;
            case ETHERNET:
                return OpenRtb.ConnectionType.ETHERNET;
            case WIFI:
                return OpenRtb.ConnectionType.WIFI;
            case CELL_UNKNOWN:
                return OpenRtb.ConnectionType.CELL_UNKNOWN;
            case CELL_2G:
                return OpenRtb.ConnectionType.CELL_2G;
            case CELL_3G:
                return OpenRtb.ConnectionType.CELL_3G;
            case CELL_4G:
                return OpenRtb.ConnectionType.CELL_4G;
            default:
                return OpenRtb.ConnectionType.WIFI;
        }
    }
}

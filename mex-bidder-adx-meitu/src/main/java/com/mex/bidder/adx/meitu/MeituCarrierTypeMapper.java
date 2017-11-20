package com.mex.bidder.adx.meitu;

import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/6/13.
 *
 * 参考资料：https://en.wikipedia.org/wiki/Mobile_country_code
 */
public class MeituCarrierTypeMapper implements DictMapper<String, TelecomOperator> {

    public static MeituCarrierTypeMapper mapper = new MeituCarrierTypeMapper();

    @Override
    public TelecomOperator toMex(String input) {
        if (null == input) {
            return TelecomOperator.OTHERS;
        }
        switch (input){
            case "46000":
                return TelecomOperator.CHINA_MOBILE;
            case "46001":
                return TelecomOperator.CHINA_UNICOM;
            case "46002":
            return TelecomOperator.CHINA_MOBILE;
            case "46003":
                return TelecomOperator.CHINA_TELECOM;
            case "46005":
                return TelecomOperator.CHINA_TELECOM;
            case "46006":
                return TelecomOperator.CHINA_UNICOM;
            case "46007":
                return TelecomOperator.CHINA_MOBILE;
            case "46008":
                return TelecomOperator.CHINA_MOBILE;
            case "46009":
                return TelecomOperator.CHINA_UNICOM;
            case "46011":
                return TelecomOperator.CHINA_TELECOM;
            default:
                return TelecomOperator.OTHERS;
        }
    }
}

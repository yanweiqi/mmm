package com.mex.bidder.adx.adview;

import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.dict.DictMapper;
import com.mex.bidder.protocol.CarrierType;

/**
 * xuchuahao
 * on 2017/4/1.
 *
 * 46000、46002、46007=>中国移动 46001、46006=>中国联通 46003、46005=>中国电信
 */
public class AdviewCarrierTypeMapper implements DictMapper<String, TelecomOperator> {


    public static AdviewCarrierTypeMapper mapper = new AdviewCarrierTypeMapper();

    @Override
    public TelecomOperator toMex(String input) {
        if (null == input) {
            return TelecomOperator.OTHERS;
        }

        switch (input) {
            case "46000":
                return TelecomOperator.CHINA_MOBILE;
            case "46002":
                return TelecomOperator.CHINA_MOBILE;
            case "46007":
                return TelecomOperator.CHINA_MOBILE;
            case "46001":
                return TelecomOperator.CHINA_UNICOM;
            case "46006":
                return TelecomOperator.CHINA_UNICOM;
            case "46003":
                return TelecomOperator.CHINA_TELECOM;
            case "46005":
                return TelecomOperator.CHINA_TELECOM;
            case "-1":
                return TelecomOperator.OTHERS;
            default:
                return TelecomOperator.OTHERS;
        }
    }
}

package com.mex.bidder.adx.zplay;

import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/4/5.
 */

/**
 "46000" || "46002") || "46007" //中国移动
 "46001" //中国联通
 "46003" //中国电信
 */
public class ZplayCarrierTypeMapper implements DictMapper<String,TelecomOperator> {

    public static ZplayCarrierTypeMapper mapper = new ZplayCarrierTypeMapper();

    @Override
    public TelecomOperator toMex(String input) {
        if (null==input){
            return TelecomOperator.OTHERS;
        }

        switch (input){
            case "46000":
                return TelecomOperator.CHINA_MOBILE;
            case "46002":
                return TelecomOperator.CHINA_MOBILE;
            case "46007":
                return TelecomOperator.CHINA_MOBILE;
            case "46001":
                return TelecomOperator.CHINA_UNICOM;
            case "46003":
                return TelecomOperator.CHINA_TELECOM;
            case "-1":
                return TelecomOperator.OTHERS;
            default:
                return TelecomOperator.OTHERS;
        }
    }
}

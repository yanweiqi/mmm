package com.mex.bidder.adx.gy;

import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/4/5.
 */

/**
 *
 China Mobile	CN	China	70120
 China Telecom	CN	China	70121
 UNICOM	CN	China	70123

 */
public class GyCarrierTypeMapper implements DictMapper<String,TelecomOperator> {

    public static GyCarrierTypeMapper mapper = new GyCarrierTypeMapper();

    @Override
    public TelecomOperator toMex(String input) {

        if (null== input){
            return TelecomOperator.OTHERS;
        }

        switch (input){
            case "70120":
                return TelecomOperator.CHINA_MOBILE;
            case "70121":
                return TelecomOperator.CHINA_TELECOM;
            case "70123":
                return TelecomOperator.CHINA_UNICOM;
            case "-1":
                return TelecomOperator.OTHERS;
            default:
                return TelecomOperator.OTHERS;
        }
    }
}

package com.mex.bidder.adx.iflytek;

import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.dict.DictMapper;

/**
 * xuchuahao
 * on 2017/4/1.
 *
 * 运营商 ID：
 46000 – 中国移动
 46001 – 中国联通
 46003 – 中国电信
 46020 – 中国铁通  --> fix update  把铁中转成移动
 */
public class IflytekCarrierTypeMapper implements DictMapper<String,TelecomOperator>{



    public  static IflytekCarrierTypeMapper mapper = new IflytekCarrierTypeMapper();



    @Override
    public TelecomOperator toMex(String input) {

        if (null == input){
            return TelecomOperator.OTHERS;
        }

        switch (input){
            case "46000":
                return TelecomOperator.CHINA_MOBILE;
            case "46001":
                return TelecomOperator.CHINA_UNICOM;
            case "46003":
                return TelecomOperator.CHINA_TELECOM;
            case "46020":
                return TelecomOperator.CHINA_MOBILE;
            case "-1":
                return TelecomOperator.OTHERS;
            default:
                return TelecomOperator.OTHERS;
        }
    }
}

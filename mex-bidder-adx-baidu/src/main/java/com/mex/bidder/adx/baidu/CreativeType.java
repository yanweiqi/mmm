package com.mex.bidder.adx.baidu;

/**
 * User: donghai
 * Date: 2016/11/24
 */
public class CreativeType {
    static final int TEXT = 0;
    static final int PIC = 1;
    static final int FLASH = 2;
    static final int PIC_TEXT = 4;
    static final int VIDEO = 7;


    public static boolean isBanner(int creative_type) {
        return creative_type == 1;
    }
}

package com.mex.bidder.asyncRedis;

/**
 * xuchuanao
 * on 2017/1/12.
 */
public class DemoTest {


    public static void main(String[] args) {
        String s = "adfa\r\nlkadlfaj";
        System.out.println(s);
        String bb = s.replace("\r\n","");
        System.out.println("替换后 = " + bb);
    }
}


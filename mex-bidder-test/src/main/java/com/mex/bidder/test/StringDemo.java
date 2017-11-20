package com.mex.bidder.test;

/**
 * xuchuahao
 * on 2017/7/13.
 */
public class StringDemo {

    public static void main(String[] args) {
        String a = "http://www.adidas.com.cn/campaign/m/nmd?cm_mmc=AdiDisplay_-_--_--_--_-dv:Brand-_-cn:-_-pc:&cm_mmc1=&utm_source=&utm_medium=Display&utm_campaign=&utm_content=&smtid=499827843z22vgz17w6lz2hnz0z\n";
        String b = "http://www.adidas.com.cn/campaign/m/nmd?cm_mmc=AdiDisplay_-_--_--_--_-dv:Brand-_-cn:-_-pc:&cm_mmc1=&utm_source=&utm_medium=Display&utm_campaign=&utm_content=&smtid=499911173z22vgz17w6lz2hnz0z\n";

        if (a.equals(b)){
            System.out.println("yes");
        } else {
            System.out.println("no");
        }

    }
}

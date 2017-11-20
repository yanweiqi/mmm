package com.mex.bidder.engine.model;

/**
 * User: donghai
 * Date: 2016/12/3
 */
public class OsVersionTest {
    public static void main(String[] args) {
        int version = 20407;

        System.out.println(version / 10000);
        System.out.println(version / 100 % 100);
        System.out.println(version % 10);
    }
}

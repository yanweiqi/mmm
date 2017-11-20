package com.mex.bidder.engine.util;

import org.junit.Test;

import java.util.UUID;

/**
 * User: donghai
 * Date: 2016/11/19
 */
public class MexUtilTest {
    @Test
    public void toHexString() throws Exception {

    }

    @Test
    public void uuid() throws Exception {
        System.out.println(MexUtil.uuid());
        System.out.println(MexUtil.uuid());
        System.out.println(MexUtil.uuid());
        System.out.println(UUID.randomUUID());
    }

}
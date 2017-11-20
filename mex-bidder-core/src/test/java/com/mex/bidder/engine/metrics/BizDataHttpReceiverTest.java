package com.mex.bidder.engine.metrics;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.mex.bidder.engine.bizdata.MexDataContext;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/12/4
 */
public class BizDataHttpReceiverTest {
    @Test
    public void receive() throws Exception {
        System.out.println(JSON.toJSONString(new MexDataContext()));

//        BizDataHttpReceiver receiver = new BizDataHttpReceiver();
//        RoutingContext routingContext = Mockito.mock(RoutingContext.class);
//        HttpServerRequest serverRequest = Mockito.mock(HttpServerRequest.class);
//        receiver.receive(routingContext);
    }

    @Test
    public void test1() {
        String toJSONString = JSON.toJSONString(ImmutableMap.of("hello", "donghai"));
        System.out.println(toJSONString);
    }

}
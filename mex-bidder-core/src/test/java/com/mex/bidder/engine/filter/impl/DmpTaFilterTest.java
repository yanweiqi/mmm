package com.mex.bidder.engine.filter.impl;

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Lists;
import com.mex.bidder.engine.dmp.impl.GetuiDmpServiceImpl;
import com.mex.bidder.protocol.Banner;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * user: donghai
 * date: 2017/6/14
 */
public class DmpTaFilterTest {
    @Test
    public void filter() throws Exception {
        GetuiDmpServiceImpl service = new GetuiDmpServiceImpl(Vertx.vertx(), new JsonObject());
        Thread.sleep(1000);

        Banner ad = new Banner();
        ad.setImpFreqCap(true);
        ad.setAdCampaignId(1);
        ad.setImpFreqCapCnt(2);
        ad.setImpFreqCapTimeUnit("d");
        ad.setClkFreqCap(true);
        ad.setClkFreqCapTimeUnit("d");
        ad.setClkFreqCapCnt(2);
        ad.setGetuiTaskId("task1");

        Banner banner1 = JSON.parseObject(JSON.toJSONString(ad), Banner.class);
        banner1.setGetuiTaskId("task2");
        Banner banner11 = JSON.parseObject(JSON.toJSONString(ad), Banner.class);
        banner11.setGetuiTaskId("task2");
        Banner banner2 = JSON.parseObject(JSON.toJSONString(ad), Banner.class);
        banner2.setGetuiTaskId("");
        Banner banner3 = JSON.parseObject(JSON.toJSONString(ad), Banner.class);
        banner3.setGetuiTaskId("");

        List<Banner> adList = Lists.newArrayList();
        adList.add(ad);
        adList.add(banner11);
        adList.add(banner1);
        adList.add(banner2);
        adList.add(banner3);

        CountDownLatch latch = new CountDownLatch(1);
        DmpTaFilter filter = new DmpTaFilter(service);
//        filter.filter(adList, Mockito.mock(BidRequest.class), Mockito.mock(BidResponse.class), res -> {
//            List<Banner> result = res.result();
//            System.out.println(result.size());
//            latch.countDown();
//        });

        latch.await();
    }

}
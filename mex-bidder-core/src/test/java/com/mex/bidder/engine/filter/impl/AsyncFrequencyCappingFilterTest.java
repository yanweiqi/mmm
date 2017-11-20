package com.mex.bidder.engine.filter.impl;

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Lists;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.platform.NoExchange;
import com.mex.bidder.protocol.Banner;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * User: donghai
 * Date: 2017/1/11
 */
public class AsyncFrequencyCappingFilterTest {

    RedisClient client;


    @Before
    public void setUp() throws Exception {
        Vertx vertx = Vertx.vertx();
        RedisOptions redisOptions = new RedisOptions()
                .setHost("localhost")
                .setPort(6379).setAuth("dsplocalredis");
        client = RedisClient.create(vertx, redisOptions);
    }

    @Test
    public void filter() throws Exception {


        Banner ad = new Banner();
        ad.setImpFreqCap(true);
        ad.setAdCampaignId(1);
        ad.setImpFreqCapCnt(1);
        ad.setImpFreqCapTimeUnit("d");
        ad.setClkFreqCap(true);
        ad.setClkFreqCapTimeUnit("d");
        ad.setClkFreqCapCnt(2);
        List<Banner> adList = Lists.newArrayList();
        adList.add(ad);

        CountDownLatch latch = new CountDownLatch(1);
        AsyncFrequencyCappingFilter filter = new AsyncFrequencyCappingFilter(client);
        Future<List<Banner>> filterFuture = filter.filter(adList, Mockito.mock(BidRequest.class), Mockito.mock(BidResponse.class));
        filterFuture.setHandler(rs -> {
            List<Banner> result = rs.result();
            System.out.println(result.size());
            latch.countDown();
        });

        latch.await();
    }


    @Test
    public void getFreqCap() throws Exception {

    }

    @Test
    public void doFilter() throws Exception {
        AsyncFrequencyCappingFilter filter = new AsyncFrequencyCappingFilter(client);
        CountDownLatch latch = new CountDownLatch(1);

        Banner ad = new Banner();
        ad.setAdGroupId(1);
        ad.setAdCampaignId(1);
        ad.setImpFreqCap(true);
        ad.setAdCampaignId(1);
        ad.setImpFreqCapCnt(1);
        ad.setImpFreqCapTimeUnit("d");
        ad.setClkFreqCap(true);
        ad.setClkFreqCapTimeUnit("d");
        ad.setClkFreqCapCnt(2);

        Banner ad1 = JSON.parseObject(JSON.toJSONString(ad), Banner.class);
        ad1.setImpFreqCap(true);
        ad1.setAdGroupId(2);
        ad1.setAdCampaignId(2);
        ad1.setImpFreqCapCnt(5);
        ad1.setImpFreqCapTimeUnit("d");

        BidResponse bidResponse = BidResponse.newBuilder()
                .setExchange(NoExchange.INSTANCE)
                .setHttpResponse(Mockito.mock(HttpServerResponse.class)).build();
        Set<String> keys = ImmutableSet.of("freq_i_1_1212", "freq_i_2_1212");
        HashBasedTable<Integer, String, List<Banner>> campIdToFreqKeyAdListTbl = HashBasedTable.create();
        campIdToFreqKeyAdListTbl.put(2, "freq_i_2_1212", ImmutableList.of(ad));
        campIdToFreqKeyAdListTbl.put(1, "freq_i_1_1212", ImmutableList.of(ad));
        Map<String, List<Banner>> row = campIdToFreqKeyAdListTbl.row(3);
        System.out.println(row.size());
        row.put("111",ImmutableList.of(ad));
        System.out.println(row.size());
        Future<List<Banner>> freqCapFuture = filter.getFreqCap(keys, bidResponse, campIdToFreqKeyAdListTbl);

        freqCapFuture.setHandler(rs -> {
            List<Banner> result = rs.result();
            System.out.println(result.size());
            latch.countDown();
        });

        latch.await();
    }

}
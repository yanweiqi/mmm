package com.mex.bidder.engine.filter.impl;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Sets;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.config.FreqRedis;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.AsyncAdListFilter;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.Future;
import io.vertx.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 频次
 * User: donghai
 * Date: 2016/11/17
 */
public class AsyncFrequencyCappingFilter implements AsyncAdListFilter {
    private static final Logger logger = LoggerFactory.getLogger(AsyncFrequencyCappingFilter.class);
    private static final String FREQ = "freq_";
    private static final String IMP = "i_";
    private static final String CLK = "c_";

    private RedisClient redisClient;

    @Inject
    public AsyncFrequencyCappingFilter(@FreqRedis RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public <B extends Ad> Future<List<B>> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse) {
        Future<List<B>> result = Future.future();
        if (adList.isEmpty()) {
            result.complete(Collections.emptyList());
            return result;
        }

        Set<String> redisKeys = Sets.newHashSet();
        String deviceId = RtbHelper.getDeviceId(bidRequest);

        HashBasedTable<Integer, String, List<B>> campIdToFreqKeyAdListTbl = HashBasedTable.create();
        List<B> noNeedFreqCapAdList = Lists.newArrayList();

        adList.forEach(ad -> {
            boolean noNeedFreq = true;
            if (ad.isImpFreqCap()) {
                // 曝光频次限制
                String impKey = FREQ + IMP + ad.getAdCampaignId() + "_" + deviceId;
                redisKeys.add(impKey);
                hashBasedTableComputeIfAbsent(campIdToFreqKeyAdListTbl, impKey, ad);
                noNeedFreq = false;
            }
            if (ad.isClkFreqCap()) {
                // 点击频次限制
                String clkKey = FREQ + CLK + ad.getAdCampaignId() + "_" + deviceId;
                redisKeys.add(clkKey);
                hashBasedTableComputeIfAbsent(campIdToFreqKeyAdListTbl, clkKey, ad);
                noNeedFreq = false;
            }
            if (noNeedFreq) {
                noNeedFreqCapAdList.add(ad);
            }
        });

        if (redisKeys.isEmpty()) {
            // 不限制频次
            result.complete(adList);
            return result;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<List<B>> freqCap = getFreqCap(redisKeys, bidResponse, campIdToFreqKeyAdListTbl);
        freqCap.setHandler(rs -> {
            logger.info("freq filter end time=" + stopwatch.stop());
            if (rs.succeeded()) {
                // 返回满足频次控制和不需要控制的广告列表
                noNeedFreqCapAdList.addAll(rs.result());
                result.complete(noNeedFreqCapAdList);
            } else {
                logger.error("getFreqCap error.", rs.cause());
                result.complete(noNeedFreqCapAdList);
            }
        });

        return result;

    }

    protected <B extends Ad> Future<List<B>> getFreqCap(Set<String> keys, BidResponse bidResponse,
                                                        HashBasedTable<Integer, String, List<B>> campIdToFreqKeyAdListTbl) {
        Future<List<B>> resultFuture = Future.future();
        Stopwatch stopwatch = Stopwatch.createStarted();
        redisClient.mgetMany(Lists.newArrayList(keys), res -> {
            logger.info("redis-time " + stopwatch.stop());

            if (res.succeeded()) {
                List<String> freqList = res.result().getList();
                Map<String, Integer> freqMap = zipToMap(keys, freqList);
                List<B> filteredList = doFilter(bidResponse, campIdToFreqKeyAdListTbl, freqMap);
                resultFuture.complete(filteredList);
            } else {
                logger.error("get from redis error.", res.cause());
                resultFuture.fail(res.cause());
            }

        });

        return resultFuture;
    }

    protected <B extends Ad> List<B> doFilter(BidResponse bidResponse, HashBasedTable<Integer, String, List<B>> campIdToFreqKeyAdListTbl,
                                              Map<String, Integer> freqMap) {
        List<B> result = Lists.newArrayList();

        Set<Integer> campIdSet = campIdToFreqKeyAdListTbl.rowKeySet();
        for (Integer campId : campIdSet) {
            Map<String, List<B>> row = campIdToFreqKeyAdListTbl.row(campId);

            //同一个活动下的clk,imp key
            boolean needFreqCap = false;
            List<B> campAdList = Collections.emptyList();
            for (Map.Entry<String, List<B>> entry : row.entrySet()) {
                String key = entry.getKey();
                campAdList = entry.getValue();

                Integer currentCnt = freqMap.get(key);
                Ad ad = campAdList.get(0);

                if (key.contains("_i_") && ad.getImpFreqCapCnt() <= currentCnt) {
                    needFreqCap = true;
                } else if (key.contains("_c_") && ad.getClkFreqCapCnt() <= currentCnt) {
                    needFreqCap = true;
                }
            }

            if (needFreqCap) {
                campAdList.forEach(adBase -> bidResponse.addFilterError(adBase.getCode(), FilterErrors.FILTER_FAIL_FreCtrl));
            } else {
                result.addAll(campAdList);
            }
        }

        return result;
    }

    static Map<String, Integer> zipToMap(Set<String> keys, List<String> values) {
        Map<String, Integer> result = Maps.newHashMap();
        int i = 0;
        for (String key : keys) {
            if (Objects.nonNull(values.get(i))) {
                result.put(key, toInteger(values.get(i)));
            } else {
                result.put(key, 0);
            }
            i++;
        }
        return result;
    }

    private static Integer toInteger(String s) {
        return Objects.nonNull(s) ? Integer.parseInt(s) : 0;
    }

    static <B extends Ad> void hashBasedTableComputeIfAbsent(HashBasedTable<Integer, String, List<B>> campIdToFreqKeyAdListTbl,
                                                             String colKey, B ad) {
        Map<String, List<B>> row = campIdToFreqKeyAdListTbl.row(ad.getAdCampaignId());
        if (row == null) {
            campIdToFreqKeyAdListTbl.put(ad.getAdCampaignId(), colKey, Lists.newArrayList());
            row = campIdToFreqKeyAdListTbl.row(ad.getAdCampaignId());
        }
        row.computeIfAbsent(colKey, k -> new ArrayList<>()).add(ad);
    }
}
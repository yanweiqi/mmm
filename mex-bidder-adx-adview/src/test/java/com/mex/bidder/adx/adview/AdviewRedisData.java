package com.mex.bidder.adx.adview;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mex.bidder.util.JsonHelper;
import com.mex.bidder.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.streams.ReadStream;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: donghai
 * Date: 2016/11/23
 */

public class AdviewRedisData {
    Vertx vertx;
    RedisClient client;

    public static String readFile(String fileName) {
        try (InputStream is = JsonHelper.class.getResourceAsStream( "/"+fileName);
             Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    @Test
    public void redis_data() throws InterruptedException {

        String s1 = readFile("redis.json");

        client.set("redis.data.ad.data", s1, event -> {
            if (event.succeeded()) {
                System.out.println("abc is ok!!!");
            } else {
                System.out.println("abs is error!!!");
            }
        });

        Thread.sleep(1000);
    }

    @Test
    public void adx_common_dic_data() throws InterruptedException {
        DspCommonDictData dspCommonDictData = new DspCommonDictData();
        Map<DictType, Map<String, String>> map = Maps.newHashMap();
        HashMap<String, String> osMap = Maps.newHashMap();
        osMap.put("ios", "2");
        osMap.put("iOS", "2");
        osMap.put("android", "1");
        osMap.put("Android", "1");
        map.put(DictType.OS, osMap);

        dspCommonDictData.setCommonDictMap(map);

        String s = JSON.toJSONString(dspCommonDictData);

        client.set(Const.REDIS_DATA_KEY_COMMON_DIC_DATA, s, event -> {
            System.out.println("执行............");
            if (event.succeeded()) {
                System.out.println("redis set " + Const.REDIS_DATA_KEY_COMMON_DIC_DATA + " is ok");
            } else {
                System.out.println("error !!!!!!!!!!!!!!!!!!!!!");
            }
        });

        Thread.sleep(1000);
    }

    @Test
    public void adx_redis_data() throws InterruptedException {
        DspAdData dspAdData = new DspAdData();
        Banner banner = new Banner();

        banner.setAdverId(1001);
        banner.setAdGroupId(11);
        banner.setAdCampaignId(10);
        banner.setAdverWebsite("www.baidu.com");
        banner.setCreativeId(20);
        banner.setLandingPage("www.landingpage.com");
        ArrayList<String> list = Lists.newArrayList();
        list.add("http://miaozhen.tracking.com");
        banner.setImpTrackingUrls(list);
        ArrayList<String> clickList = Lists.newArrayList();
        clickList.add("www.clicktracing.com");
        banner.setClickTrackingUrls(clickList);
        banner.setAdCampaignTotalBudget(new BigDecimal(1000));
        banner.setAdCampaignDayBudget(new BigDecimal(100));
        banner.setAdGroupDayBudget(new BigDecimal(10));
        banner.setImpFreqCapCnt(1);
        banner.setClkFreqCapCnt(1);
        banner.setImpFreqCapTimeUnit("10");
        banner.setClkFreqCapTimeUnit("5");
        banner.setAdxBidPrice(new BigDecimal(5));
        banner.setDeviceType("4");
        banner.setOsType("ios");
        banner.setOsMinVersion(6);
        banner.setOsMaxVersion(2);
        banner.setConnectType("2");
        banner.setCity("1156110000");
        banner.setMediaCat("news");
        banner.setProductName("product");
        banner.setProductId(111);
        banner.setAdxId("004");
        banner.setAdxName("adsview");
        banner.setWidth(320);
        banner.setHeight(50);
        banner.setMaterialUrl("www.me/123img.jpg");
        banner.setAdverCurrencyType("cpm");
        banner.setAdxCurrencyType("cpm");
        banner.setAdxBidMode("1");
        banner.setAdxPriceType("ecpm");


        Lists.newArrayList().add(banner);

        AdxData adxData = AdxData.create("adsview");
        adxData.addBanner(banner);

        dspAdData.addExchangeData(adxData);

        String s = JSON.toJSONString(dspAdData);
        System.out.println(s);
        client.set(Const.REDIS_DATA_KEY_AD_DATA, s, event -> {
            if (event.succeeded()) {
                System.out.println("adsview " + Const.REDIS_DATA_KEY_AD_DATA + " is ok");
            }
        });

        Thread.sleep(10000L);

    }

    @Test
    public void setAllData() throws InterruptedException {
        String data = JsonHelper.readFile("alldata.json");
        client.set(Const.REDIS_DATA_KEY_AD_DATA, data, event -> {
            if (event.succeeded()) {
                System.out.println("data set is ok !!");
            }
        });

        Thread.sleep(5000);

    }

    @Test
    public void adview_redis_data() throws InterruptedException {

        String adxCnf = JsonHelper.readFile("adview.redis.cnf.json");
        String creatives = JsonHelper.readFile("adview.redis.creatives.json");

        CountDownLatch latch = new CountDownLatch(2);
        client.hset("rtb.creatives", "content", creatives, event -> {
            System.out.println("set adx creatives ok");
            latch.countDown();
        });

        client.hset("rtb.channel.message", "adsview", adxCnf, event -> {
            System.out.println("set adx conf ok");
            latch.countDown();
        });

        latch.await(30, TimeUnit.SECONDS);
    }


    @Before
    public void setUp() throws Exception {
        // 本地redis
        vertx = Vertx.vertx();
        RedisOptions config = new RedisOptions().setHost("localhost");
//        RedisOptions config = new RedisOptions().setHost("172.17.0.132").setPort(6380).setAuth("dsptest");
        client = RedisClient.create(vertx, config);
    }
}

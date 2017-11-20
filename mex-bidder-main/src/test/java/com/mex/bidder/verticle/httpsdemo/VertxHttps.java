package com.mex.bidder.verticle.httpsdemo;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

/**
 * user: donghai
 * date: 2017/3/16
 */
public class VertxHttps {
    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions();
        options.setSsl(true);
        options.setTrustAll(true);
        options.setDefaultPort(443);


        HttpClient httpClient = vertx.createHttpClient(options);
        final int cnt = 1000;
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                runnable(httpClient, cnt);
            }).start();
        }


        Thread.currentThread().join();
        System.out.println("--END--");


    }

    private static void runnable(HttpClient httpClient, int cnt) {

        String url = "https://dsptrack.ad-mex.com/adImp?requestid=test-123&adgroupid=99999&netid=004&netname=adsview&devicetype=TABLET&os=iOS&connectiontype=WIFI&material_id=423&adid=NA&idfa=NA&android_id=NA&android_id_md5=5c1f13975712ef6f60113d99fdd4de36&android_id_sha1=ef14ed2c244b89169f6ac5d682f929f6ca4e0d28&imei=NA&imei_md5=NA&imei_sha1=NA&deviceID=5c1f13975712ef6f60113d99fdd4de36&remote_addr=117.80.94.246&cur_adv=RMB&cur_adx=RMB&adver_id=15&campaign_id=20&resptimestamp=20170309153500983&height=90&width=728&make=Apple&model=iPad&bundle=com.juji.danjimajiang&ip=117.80.94.246&app_name=%E5%9B%9B%E5%B7%9D%E9%BA%BB%E5%B0%86&material_type=banner&price=igz-sVoBAAAydExMc34wfltrPjs-CcXU9KPPeQ";
        for (int i = 0; i < cnt; i++) {
            httpClient.getAbs(url, res -> {
                if (res.statusCode() == 200) {
                    System.out.println("send ok");
                } else {
                    System.out.println("send failed");
                }
            }).end();
        }


    }
}

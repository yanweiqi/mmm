package com.mex.bidder.adx.iflytek;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

/**
 * xuchuanao
 * on 2017/2/23.
 */
public class IflytekGet {

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
//                .setDefaultHost("localhost").setDefaultPort(9080);
                .setDefaultHost("123.56.14.96").setDefaultPort(80);
//                .setDefaultHost("101.200.34.111").setDefaultPort(9080);

        HttpClient httpClient = vertx.createHttpClient(options);
//        String url = "http://123.56.23.129/adImp?requestid=test&adgroupid=23&netid=025&netname=adsiflytek&devicetype=HIGHEND_PHONE&os=android&connectiontype=CELL_4G&material_id=1554&adid=NA&idfa=NA&android_id=NA&android_id_md5=NA&android_id_sha1=NA&imei=NA&imei_md5=D41D8CD98F00B204E9800998ECF8427E&imei_sha1=NA&deviceID=D41D8CD98F00B204E9800998ECF8427E&remote_addr=60.24.65.164&cur_adv=RMB&cur_adx=RMB&adver_id=11&campaign_id=10&resptimestamp=20170223163120704&price=${AUCTION_PRICE}";
        String url = "http://123.56.19.131/adClick?requestid=test-clk&amp;adgroupid=23&amp;netid=025&amp;netname=adsiflytek&amp;devicetype=HIGHEND_PHONE&amp;os=android&amp;connectiontype=CELL_4G&amp;material_id=1554&amp;adid=NA&amp;idfa=NA&amp;android_id=NA&amp;android_id_md5=NA&amp;android_id_sha1=NA&amp;imei=NA&amp;imei_md5=D41D8CD98F00B204E9800998ECF8427E&amp;imei_sha1=NA&amp;deviceID=D41D8CD98F00B204E9800998ECF8427E&amp;remote_addr=60.24.65.164&amp;cur_adv=RMB&amp;cur_adx=RMB&amp;adver_id=11&amp;campaign_id=10&amp;resptimestamp=20170223193958933";

        httpClient.getAbs(url,resp->{
            if(200==resp.statusCode()){
                resp.bodyHandler(body ->{
                    System.out.println("send ok!");
                    System.out.println(body.toString());
                });
            } else {
                int status = resp.statusCode();
                System.out.println("status="+status);

            }
        }).end();

        Thread.sleep(5000L);

    }
}

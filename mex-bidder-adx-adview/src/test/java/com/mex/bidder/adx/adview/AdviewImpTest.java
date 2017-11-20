package com.mex.bidder.adx.adview;

import com.google.common.base.Stopwatch;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

/**
 * xuchuahao
 * on 2017/6/2.
 */
public class AdviewImpTest {
    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost("dsptrack.ad-mex.com").setDefaultPort(80);

        String url = "http://dsptrack.ad-mex.com/adImp?requestid=0bymQo1DgBU83xNyqM3a8Fp429wQ7n&adgroupid=287&netid=018&netname=adszp&devicetype=TABLET&os=android&connectiontype=WIFI&material_id=500642&adid=NA&android_id=951b81d5fed114ee&android_id_md5=430856dc4674673168ae4a3aab0d12f2&android_id_sha1=a6c347e229cd24a6d91ddaa57eb9c9856c56593f&imei=NA&imei_md5=NA&imei_sha1=NA&deviceID=951b81d5fed114ee&mac=78%3A40%3AE4%3A29%3A91%3A91&mac_md5=336c2df98fa8270fb8f888e92db10b80&mac_sha1=3b42421198d24fb0a8cf365eddf61fcfeca416b7&remote_addr=111.9.252.163&cur_adv=RMB&cur_adx=RMB&adver_id=500013&campaign_id=48&resptimestamp=20170602100132719&height=50&width=320&make=samsung&model=SM-T210&bundle=com.brianbaek.popstar&ip=111.9.252.163&app_name=%E5%9B%BD%E5%86%85-%E5%AE%89%E5%8D%93-%E6%B6%88%E7%81%AD%E6%98%9F%E6%98%9F%E5%AE%98%E6%96%B9%E6%AD%A3%E7%89%88&material_type=banner&productid=43&taid=0&tatype=0&tamid=0&price=_cYwWfUagGPJiPO8fy0YNA";

        // 发送请求
            HttpClient client = vertx.createHttpClient(options);
            Stopwatch stopwatch = Stopwatch.createStarted();
             for (int i = 0; i < 400; i++) {
                 client.getAbs(url.replace("0bymQo1DgBU83xNyqM3a8Fp429wQ7n","287true-test"+i),res->{
                     int i1 = res.statusCode();
                     System.out.println("status="+i1);
                 }).end();
             }

        try {
            Thread.sleep(10l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

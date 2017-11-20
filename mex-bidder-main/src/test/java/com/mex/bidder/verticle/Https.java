package com.mex.bidder.verticle;

/**
 * xuchuanao
 * on 2017/3/16.
 */
public class Https {

    //    private static final String HTTPS_URL = "https://dsptrack.ad-mex.com/adClick?requestid=0bug6s1COr6N3cjx0N0EQ5ql48KBnK&adgroupid=120&netid=018&netname=adszp&devicetype=HIGHEND_PHONE&os=ios&connectiontype=WIFI&material_id=487&adid=FB9340D8-49CD-4DFF-8149-B3A5B8043CEC&android_id=NA&android_id_md5=NA&android_id_sha1=e8c47bd5b9df9d539d31b336627fdd50d7f63269&imei=NA&imei_md5=NA&imei_sha1=NA&deviceID=FB9340D8-49CD-4DFF-8149-B3A5B8043CEC&mac=02%3A00%3A00%3A00%3A00%3A00&mac_md5=NA&mac_sha1=c1976429369bfe063ed8b3409db7c7e7d87196d9&remote_addr=180.111.70.106&cur_adv=RMB&cur_adx=RMB&adver_id=17&campaign_id=22&resptimestamp=20170316165009474&height=50&width=320&make=Apple&model=iPhone8%2C1&bundle=com.xunxin.popstarv&ip=180.111.70.106&app_name=%E6%8D%95%E9%B1%BC%E8%BE%BE%E4%BA%BAiOS&material_type=banner";
    private static final String HTTPS_URL = "https://dsptrack.ad-mex.com/adClick?requestid=1111111111&adgroupid=120&netid=018&netname=adszp&devicetype=HIGHEND_PHONE&os=ios&connectiontype=WIFI&material_id=487&adid=FB9340D8-49CD-4DFF-8149-B3A5B8043CEC&android_id=NA&android_id_md5=NA&android_id_sha1=e8c47bd5b9df9d539d31b336627fdd50d7f63269&imei=NA&imei_md5=NA&imei_sha1=NA&deviceID=FB9340D8-49CD-4DFF-8149-B3A5B8043CEC&mac=02%3A00%3A00%3A00%3A00%3A00&mac_md5=NA&mac_sha1=c1976429369bfe063ed8b3409db7c7e7d87196d9&remote_addr=180.111.70.106&cur_adv=RMB&cur_adx=RMB&adver_id=17&campaign_id=22&resptimestamp=20170316165009474&height=50&width=320&make=Apple&model=iPhone8%2C1&bundle=com.xunxin.popstarv&ip=180.111.70.106&app_name=%E6%8D%95%E9%B1%BC%E8%BE%BE%E4%BA%BAiOS&material_type=banner";


    public static void main(String[] args) throws Exception {
        int count = readConf(args);
        for (int i = 0; i < count; i++) {
            new Thread(new SendRunable()).run();
            new Thread(new SendRunable()).run();
            new Thread(new SendRunable()).run();
            new Thread(new SendRunable()).run();
            new Thread(new SendRunable()).run();
        }
    }

    private static int readConf(String[] args) {
        int count = 0;
        if (null != args && args.length > 0) {
            count = Integer.parseInt(args[0]);
            System.out.println("outside conf, count=" + count);
        } else {
            count = 10;
            System.out.println("inside conf, count=" + count);
        }

        return count;
    }
}

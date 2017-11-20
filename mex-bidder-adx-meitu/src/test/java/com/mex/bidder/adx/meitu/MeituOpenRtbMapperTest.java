package com.mex.bidder.adx.meitu;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonReader;
import com.meitu.openrtb.MeituOpenRtb;

import java.io.IOException;

/**
 * xuchuahao
 * on 2017/6/13.
 */
public class MeituOpenRtbMapperTest {

    static OpenRtbJsonReader openRtbJsonReader;

    public static void main(String[] args) {
        String req = "{\"id\": \"meitu-test-123\",\"imp\": [{\"id\": \"imp123\",\"banner\": {\"w\": 640,\"h\": 100,\"id\": \"banner1231231\",\"pos\": \"ABOVE_THE_FOLD\",\"battr\": [\"AD_CAN_BE_SKIPPED\"],\"mimes\": [\"image/jpg\",\"image/gif\"],\"format\": [{\"w\": 320,\"h\": 50}]},\"displaymanager\": \"1.0\",\"instl\": true,\"tagid\": \"ad123\",\"bidfloor\": 2.0,\"bidfloorcur\": \"CNY\",\"clickbrowser\": true,\"secure\": false}],\"app\": {\"id\": \"app123\",\"name\": \"xiaomi\",\"domain\": \"xiaomi.com\",\"cat\": [\"CATE001\"],\"sectioncat\": [\"CATE002\"],\"pagecat\": [\"CATE003\"],\"ver\": \"1.0\",\"bundle\": \"1231l2j3klj\",\"paid\": true},\"device\": {\"dnt\": true,\"ua\": \"ua\",\"ip\": \"172.24.12.33\",\"geo\": {\"lat\": 11.23323,\"lon\": 44.21312,\"country\": \"china\",\"region\": \"ll\"},\"didsha1\": \"imeisha1123123\",\"didmd5\": \"imeimd5123123\",\"dpidsha1\": \"androididsha1123123\",\"dpidmd5\": \"androididmd51231231\",\"ipv6\": \"ipv6lkfdld\",\"carrier\": \"\",\"language\": \"zh\",\"make\": \"apple\",\"model\": \"iphone\",\"os\": \"ios\",\"osv\": \"10.11\",\"js\": true,\"devicetype\": \"MOBILE\",\"macsha1\": \"macsha1qkejqlj\",\"macmd5\": \"macmd512312\",\"hwv\": \"iPhone 5s\",\"w\": 1024,\"h\": 1000,\"ppi\": 5000},\"user\": {\"id\": \"userid123123\",\"yob\": 19901203,\"gender\": \"M\"},\"at\": \"SECOND_PRICE\",\"tmax\": 100,\"wseat\": [\"white1\",\"white2\"],\"allimps\": true,\"cur\": [\"RMB\"],\"bcat\": [\"CATE001\",\"CATE002\"],\"bapp\": [\"com.popstar\",\"com.game\"],\"test\": true}";
        String meituReq = "{\"app\":{\"ver\":\"1.2\",\"storeurl\":\"https://itunes.apple.com/cn/app/id902345501?l=zh&mt=8\",\"cat\":[10505],\"paid\":0,\"name\":\"App Name\",\"id\":\"9d66d9249cc5bd549b0e68b9fedc69a7\",\"bundle\":\"yourcompany.com.app\"},\"at\":2,\"id\":\"--\",\"imp\":[{\"bidfloor\":6000,\"banner\":{\"pos\":1,\"w\":640,\"h\":100},\"bidfloorcur\":\"RMB\",\"id\":\"5cdef32a55397c48b8baeb3cee0c5b5c\",\"instl\":0}],\"device\":{\"os\":\"ios\",\"sw\":768,\"s_density\":2,\"ip\":\"119.57.32.71\",\"js\":1,\"language\":\"zh\",\"dnt\":0,\"ua\":\"Mozilla/5.0 (iPhone\",\"devicetype\":1,\"geo\":{\"lon\":116.4736795,\"type\":1,\"lat\":39.9960702},\"carrier\":\"46000\",\"osv\":\"7.0.6\",\"sh\":1024,\"model\":\"iPhone5,1\",\"connectiontype\":2,\"make\":\"Apple\",\"dpidsha1\":\"7c222fb2927d828af22f592134e8932480637c0d\"},\"user\":{}}";

        try {
            OpenRtb.BidRequest bidRequest = openRtbJsonReader.readBidRequest(req);
            System.out.println("lll");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

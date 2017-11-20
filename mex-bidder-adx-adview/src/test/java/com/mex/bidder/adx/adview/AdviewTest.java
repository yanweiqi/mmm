package com.mex.bidder.adx.adview;

import io.vertx.core.json.Json;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * User: donghai
 * Date: 2016/11/15
 */
public class AdviewTest {
    String jsonReq = "{" +
            "    \"id\": \"fae6e87e932c29a0c177512151114f22\"," +
            "    \"at\": 2," +
            "    \"app\": {" +
            "        \"id\": \"9d66d9249cc5bd549b0e68b9fedc69a7\"," +
            "        \"paid\": 0," +
            "        \"cat\": [" +
            "            10505" +
            "        ]," +
            "        \"storeurl\": \"https://itunes.apple.com/cn/app/id902345501?l=zh&mt=8\"," +
            "        \"name\": \"App Name\"," +
            "        \"bundle\": \"yourcompany.com.app\"," +
            "        \"ver\": \"1.2\"" +
            "    }," +
            "    \"imp\": [" +
            "        {" +
            "            \"id\": \"5cdef32a55397c48b8baeb3cee0c5b5c\"," +
            "            \"bidfloor\": 6000," +
            "            \"instl\": 0," +
            "            \"bidfloorcur\": \"RMB\"," +
            "            \"banner\": {" +
            "                \"w\": 320," +
            "                \"h\": 250," +
            "                \"pos\": 1" +
            "            }" +
            "        }" +
            "    ]," +
            "    \"device\": {" +
            "        \"os\": \"iOS\"," +
            "        \"model\": \"iPhone5,1\"," +
            "        \"geo\": {" +
            "            \"lon\": 116.4736795," +
            "            \"type\": 1," +
            "            \"lat\": 39.9960702" +
            "        }," +
            "        \"osv\": \"7.0.6\"," +
            "        \"js\": 1," +
            "        \"dnt\": 0," +
            "        \"sh\": 1024," +
            "        \"ip\": \"10.23.45.67\"," +
            "        \"s_density\": 2," +
            "        \"connectiontype\": 2," +
            "        \"dpidsha1\": \"7c222fb2927d828af22f592134e8932480637c0d\"," +
            "        \"ua\": \"Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_6 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Mobile/9B206\"," +
            "        \"carrier\": \"46000\"," +
            "        \"language\": \"zh\"," +
            "        \"make\": \"Apple\"," +
            "        \"sw\": 768" +
            "    }," +
            "    \"user\": {}" +
            "}";


    String jsonRes = "";


    String openRtb = "{" +
            "  \"id\" : \"resp\"," +
            "  \"seatbid\" : [ {" +
            "    \"bid\" : [ {" +
            "      \"id\" : \"bid\"," +
            "      \"impid\" : \"imp\"," +
            "      \"price\" : 19.95," +
            "      \"adid\" : \"adid\"," +
            "      \"nurl\" : \"http://iwon.com\"," +
            "      \"adm\" : {" +
            "        \"ver\" : \"1.0\"," +
            "        \"link\" : { }," +
            "        \"imptrackers\" : [ \"http://my.imp.tracker\" ]" +
            "      }," +
            "      \"adomain\" : [ \"http://myads.com\" ]," +
            "      \"bundle\" : \"com.google.testapp\"," +
            "      \"iurl\" : \"http://mycdn.com/ad.gif\"," +
            "      \"cid\" : \"cid\"," +
            "      \"crid\" : \"crid\"," +
            "      \"cat\" : [ \"IAB10-2\" ]," +
            "      \"attr\" : [ 12 ]," +
            "      \"dealid\" : \"deal\"," +
            "      \"w\" : 100," +
            "      \"h\" : 80," +
            "      \"ext\" : {" +
            "        \"test1\" : \"data1\"" +
            "      }" +
            "    } ]," +
            "    \"seat\" : \"seat\"" +
            "  } ]," +
            "  \"bidid\" : \"bid\"," +
            "  \"cur\" : \"USD\"," +
            "  \"customdata\" : \"mydata\"," +
            "  \"nbr\" : 1" +
            "}";

    @Test
    public void testJSONDecode() {
        HashMap reqhashMap = Json.decodeValue(jsonReq, HashMap.class);
        System.out.println(reqhashMap.get("id"));
        HashMap reshashMap = Json.decodeValue(jsonRes, HashMap.class);
        System.out.println(reshashMap.get("id"));
    }

    @Test
    public void testRequestReader() throws IOException {
//        OpenRtbJsonFactory factory = OpenRtbJsonFactory.create();
//        //factory.register()
//        OpenRtb.BidRequest req2 = factory.newReader().readBidRequest(jsonReq);
//        System.out.println(req2.getId());
    }

    @Test
    public void testResponseWriter() throws IOException {
//        OpenRtbJsonFactory factory = OpenRtbJsonFactory.create();
//        openRtb = openRtb.replace(" ", "");
//        OpenRtb.BidResponse bidResponse = factory.newReader().readBidResponse(openRtb);
////        bidResponse.toBuilder().setExtension();
//
//        bidResponse.getSeatbid(0).getBid(0).getPrice();
//        System.out.println("rid=" + bidResponse.getId());
//
//        String s = factory.newWriter().writeBidResponse(bidResponse);
//        System.out.printf(s);
    }

}

package com.mex.bidder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.Descriptors;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;

import java.util.Iterator;

/**
 * xuchuahao
 * on 2017/3/28.
 */
public class JsonTest {

    private static final String pakageName;

    static {
        pakageName =  MexOpenRtbExt.getDescriptor().getPackage()+".";
    }

    public static void main(String[] args) {
        String bidrequest = "{\"com.mex.bidder.api.openrtb.nbr\":[\"1-1-1-3\",\"4-5-6-6-7\"],\"id\": \"0btSm51CPVYI0k2eDc3CxZl03hf3eZ\",\"imp\": [{\"id\": \"1\",\"banner\": {\"w\": 640,\"h\": 100,\"pos\": \"UNKNOWN\"},\"instl\": false,\"tagid\": \"zapdaf80f283052910007a6bf88c7cda433c35f1ca4\",\"bidfloor\": 1.95,\"bidfloorcur\": \"CNY\"}],\"app\": {\"id\": \"1000305\",\"name\": \"ios-官方正版-AS-iphone\",\"ver\": \"4.4.7\",\"bundle\": \"com.zplay.popstar\"},\"device\": {\"dnt\": false,\"ua\": \"PopStar!/4.4.71CFNetwork/711.2.23Darwin/14.0.0\",\"ip\": \"211.161.240.248\",\"geo\": {\"country\": \"CHN\",\"region\": \"上海\",\"city\": \"上海\",\"type\": \"IP\"},\"didsha1\": \"\",\"dpidsha1\": \"c637c2f383d5e4703a10bd0d6c2de70b3bd4c27e\",\"make\": \"Apple\",\"model\": \"iPhone5,4\",\"os\": \"ios\",\"osv\": \"8.2\",\"connectiontype\": \"WIFI\",\"devicetype\": \"HIGHEND_PHONE\",\"macsha1\": \"c1976429369bfe063ed8b3409db7c7e7d87196d9\",\"w\": 320,\"h\": 568,\"ppi\": 2,\"pxratio\": 2.0,\"com.mex.bidder.api.openrtb.plmn\": \"46000\",\"com.mex.bidder.api.openrtb.imei\": \"\",\"com.mex.bidder.api.openrtb.mac\": \"02:00:00:00:00:00\",\"com.mex.bidder.api.openrtb.android_id\": \"\",\"com.mex.bidder.api.openrtb.adid\": \"EBA0BC21-5DC6-4B28-8150-F3569B89C5CF\"},\"com.mex.bidder.api.openrtb.version\": 1,\"com.mex.bidder.api.openrtb.need_https\": false,\"com.mex.bidder.api.openrtb.netname\": \"adszp\"}";

        JSONObject jsonObject = JSONObject.parseObject(bidrequest);

        Object o = traveseJson(jsonObject);
        System.out.println("aiyouaiyouaiyou========"+o.toString());
        
    }

    public static void main1(String[] args) {
        System.out.println(traveseJson("传入要遍历的json"));
// 生成的JSON数据1
//      {
//          "QQ":["742981086@qq.com","742981086"],
//          "age":22,
//          "name":"aflyun",
//          "hobby":["编程","看书","徒步","爬山","游泳"],
//          "adderss":{"省份":"广东","市":"惠州","国籍":"中国"}
//      }
        //创建 一个JsonObjec对象
        JSONObject resJsonObj = new JSONObject();
        //姓名
        resJsonObj.put("name", "aflyun");
        //年龄
        resJsonObj.put("age", 22);
        //联系方式
        JSONArray arryQq = new JSONArray();
        arryQq.add("742981086@qq.com");
        arryQq.add("742981086");
        resJsonObj.put("QQ", arryQq);
        //地址 map
        JSONObject jsonAdress = new JSONObject();
        jsonAdress.put("国籍", "中国");
        jsonAdress.put("省份", "广东");
        jsonAdress.put("市", "惠州");
        resJsonObj.put("adderss", jsonAdress);
        //生成数组array
        JSONArray jArray = new JSONArray();
        jArray.add("编程");
        jArray.add("看书");
        jArray.add("徒步");
        jArray.add("爬山");
        jArray.add("游泳");
        resJsonObj.put("hobby", jArray);

        System.out.println(resJsonObj);

        System.err.println(traveseJson(resJsonObj));

//数组类型的json格式数据生成
        //[
        // {"hello":"你好"},
        //     [
        //         {"在干嘛":"编程"},
        //         ["睡觉了吗","没有","不想睡","醒来了"]
        //     ]
        //]

        JSONArray retJson = new JSONArray();
        //hello
        JSONObject aJosn = new JSONObject();
        aJosn.put("hello", "你好");
        retJson.add(aJosn);
        //数组在干嘛和睡觉了吗 组装[]
        JSONArray jsa = new JSONArray();
        JSONObject jOne = new JSONObject();
        jOne.put("在干嘛", "编程");
        JSONArray jTwo = new JSONArray();
        jTwo.add("没有");
        jTwo.add("不想睡");
        jTwo.add("");

    /*    JSONObject jOne1 = new JSONObject("醒来了");
        jOne1.put("睡觉了吗", jTwo);
        jsa.put(jOne).put(jOne1);
        //将组装好的数据放入要返回的json数组中
        retJson.put(jsa);*/

        System.out.println("------" + retJson);
        System.err.println("------" + traveseJson(retJson));


    }

    private static String formatKey(String originalKey){

        if(originalKey.contains(pakageName)){
            return originalKey.split(pakageName)[1];
        }
        return originalKey;
    }


    /**
     * 遍历json格式数据
     *
     * @param json
     * @return
     */
    public static Object traveseJson(Object json) {

        if (json == null) {
            return null;
        }
        if (json instanceof JSONObject) {//json 是一个map
            //创建一个json对象
            JSONObject jsonObj = new JSONObject();
            //将json转换为JsonObject对象
            JSONObject jsonStr = (JSONObject) json;
            //迭代器迭代 map集合所有的keys

            Iterator it = jsonStr.keySet().iterator();
            while (it.hasNext()) {
                //获取map的key
                String key = (String) it.next();
                //得到value的值
                Object value = jsonStr.get(key);
                //System.out.println(value);
                //递归遍历
                key = formatKey(key);
                jsonObj.put(key, traveseJson(value));

            }
            return jsonObj;

        } else if (json instanceof JSONArray) {// if  json 是 数组
            JSONArray jsonAry = new JSONArray();
            JSONArray jsonStr = (JSONArray) json;
            //获取Array 的长度
            int length = jsonStr.size();
            for (int i = 0; i < length; i++) {

                jsonAry.add(traveseJson(jsonStr.get(i)));
            }

            return jsonAry;

        } else {//其他类型

            return json;
        }

    }
}

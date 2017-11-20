package com.mex.bidder.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mex.bidder.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * 用于测试广告请求、曝光、点击业务
 * <p>
 * user: donghai
 * date: 2016/12/26
 */
public class AdviewAdxMock extends AdxMock {
    private static final Logger logger = LoggerFactory.getLogger(AdviewAdxMock.class);

    public static void main(String[] args) throws Exception {

        AdviewAdxMock mock = new AdviewAdxMock();

        mock.run();
        System.out.println("- EOF -");
    }

    @Override
    protected void run() {
        send("adsview","%%WIN_PRICE%%","nurl");


    }

    @Override
    protected String readJson() {
        return JsonHelper.readFile("adview/adview.req.json");
    }

    @Override
    protected String getImpUrl(JSONObject bid) {
        JSONArray impUrls = bid.getJSONArray("nurl");
        if(!impUrls.isEmpty()){
            return impUrls.getString(0);
        }

        return null;
    }

}

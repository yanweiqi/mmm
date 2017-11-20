package com.mex.bidder.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by Administrator on 2016/12/27.
 */
public class IflytekMock extends AdxMock {

    Logger logger = LoggerFactory.getLogger(IflytekMock.class);

    public static void main(String[] args) throws Exception {
        IflytekMock iflytekMock = new IflytekMock();

        iflytekMock.run();
    }

    @Override
    protected void run() {

        send("adsiflytek", "${AUCTION_PRICE}", "impress");

    }

    @Override
    protected String readJson() {
        return JsonHelper.readFile("iflytek/iflytek.req.json");
    }

    @Override
    protected String getImpUrl(JSONObject bid) {
        JSONArray impUrls = bid.getJSONObject("banner_ad").getJSONArray("impress");
        if (!impUrls.isEmpty()){
            return impUrls.getString(0);
        }

        return null;
    }
}

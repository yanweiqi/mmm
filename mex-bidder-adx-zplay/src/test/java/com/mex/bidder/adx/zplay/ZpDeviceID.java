package com.mex.bidder.adx.zplay;

import com.mex.bidder.util.JsonHelper;
import io.vertx.core.json.JsonObject;

/**
 * xuchuanao
 * on 2017/1/18.
 */
public class ZpDeviceID {

    public static void main(String[] args) {
        String req = JsonHelper.readFile("zplay.req-2.json");
        JsonObject reqJson = new JsonObject(req);
        JsonObject device = reqJson.getJsonObject("device");


    }
}

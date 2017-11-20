package com.mex.bidder.asyncRedis;

import com.mex.bidder.util.JsonHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

/**
 * xuchuanao
 * on 2017/1/11.
 */
public class AsyncRedisTest {

    JsonObject conf;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServerOptions serverOpts = new HttpServerOptions();
        serverOpts.setAcceptBacklog(65536);
        serverOpts.setTcpNoDelay(true);
        HttpServer server = vertx.createHttpServer(serverOpts);
//        vertx.deployVerticle();
    }


    public void init(){
        Vertx vertx = Vertx.vertx();
        String confJson = JsonHelper.readFile("main.cnf.json");
        conf = new JsonObject(confJson);
    }

}

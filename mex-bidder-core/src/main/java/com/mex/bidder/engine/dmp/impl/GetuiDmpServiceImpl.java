package com.mex.bidder.engine.dmp.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.util.MD5Utils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

/**
 * 个推对接
 * user: donghai
 * date: 2017/6/14
 */
@Singleton
public class GetuiDmpServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(GetuiDmpServiceImpl.class);

    private static final int TOKEN_RENEW_RETRY = 3;
    private static final int QUERY_TIMEOUT_MILLIS = 2000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 60 * 1000;       // 1m
    private static final long TOKEN_RENEW_TIME_INTERVAL = 20 * 60 * 1000L; // 20m
    private static final String AUTH_URL = "/accesser/auth";
    private static final String QUERY_URL = "/user/tasklist";
    private static final String AUTH_URL_QUERY = "user_code=%s&sign1=%s&sign2=%s&timestamp=%s";

    private final String userCode;
    private final String authCode;
    private final String host;
    private final int port;
    private final int timeout;
    private final int maxPoolSize;
    private String authToken;
    private boolean renewTokenFinished;

    private Vertx vertx;
    private HttpClientOptions options;
    private HttpClient queryClient;

    @Inject
    public GetuiDmpServiceImpl(Vertx vertx, JsonObject config) {

        this.vertx = vertx;
        this.userCode = config.getString("userCode");
        this.authCode = config.getString("authCode");
        this.host = config.getString("host");
        this.port = config.getInteger("port");
        this.timeout = config.getInteger("timeout");
        this.maxPoolSize = config.getInteger("maxPoolSize");

        options = new HttpClientOptions();
        options.setTcpNoDelay(true);
        options.setTcpKeepAlive(true);
        options.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        options.setDefaultHost(host);
        options.setDefaultPort(port);
        options.setMaxPoolSize(maxPoolSize);

        queryClient = vertx.createHttpClient(options);
        logger.info("host=" + host + ",port=" + port);

        // 启动时主动获取
        renewToken();

        // 定时更新TOKEN
        this.vertx.setPeriodic(TOKEN_RENEW_TIME_INTERVAL, r -> renewToken());
    }


    public Future<AsyncResult<List<String>>> retrieve(BidRequest bidRequest) {
        Future<AsyncResult<List<String>>> result = Future.future();

        // 如果还没获取到token直接返回
        if (Strings.isNullOrEmpty(authToken)) {
            result.complete(Future.failedFuture("token is empty"));
            logger.info("token is empty");
            renewToken();
            return result;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, String> reqData = getPostData(bidRequest);
        String reqJsonData = Json.encode(reqData);

        HttpClientRequest clientRequest = queryClient.post(QUERY_URL, res -> {
            res.exceptionHandler(t -> {
                logger.error("query response error.", t);
                queryClient.close();
                renewQueryClient();
                result.complete(Future.failedFuture(t));
                logger.info("query error, time=" + stopwatch.stop());
            });

            res.bodyHandler(body -> {
                if (res.statusCode() == 200) {
                    String data = body.toString("utf-8");
                    logger.info("reqData=" + reqJsonData + ", getui query data=" + data);
                    JsonObject jsonObject = new JsonObject(data);
                    String code = jsonObject.getString("code");
                    if ("0".equals(code)) {
                        // 返回真实数据
                        JsonArray taskArray = jsonObject.getJsonArray("data");
                        logger.info("dmp query end, time=" + stopwatch);
                        result.complete(Future.succeededFuture((taskArray.getList())));
                    } else {
                        logger.error("ge tui query error, code=" + code);
                        result.complete(Future.failedFuture("return code not eq to 0"));
                    }
                    stopwatch.stop();
                } else {
                    logger.error("ge tui query error, statusCode=" + res.statusCode());
                    stopwatch.stop();
                    authToken = "";
                    renewTokenFinished = false;
                    renewToken();
                    result.complete(Future.failedFuture("statusCode not eq to 200"));
                }

            });

        });

        clientRequest.exceptionHandler(t -> {
            logger.error("query request error.", t);
            queryClient.close();
            renewQueryClient();
            logger.info("query error, time=" + stopwatch.stop());
            result.complete(Future.failedFuture(t));
        });

        clientRequest.setTimeout(timeout);
        clientRequest.putHeader("Accept", "application/vnd.dmp.v1+json");
        clientRequest.putHeader("Authorization", "Bearer " + authToken);
        clientRequest.end(reqJsonData);

        return result;

    }


    /**
     * 更新TOKEN
     */
    private void renewToken() {

        logger.info("renewToken start");
        Stopwatch stopwatch = Stopwatch.createStarted();
        HttpClientOptions options = new HttpClientOptions();
        options.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        options.setDefaultHost(host);
        options.setDefaultPort(port);
        HttpClient authClient = vertx.createHttpClient(options);

        String timestamp = System.currentTimeMillis() + "";
        String sign1 = MD5Utils.MD5(userCode + MD5Utils.MD5(timestamp));
        String sign2 = MD5Utils.MD5(userCode + authCode + MD5Utils.MD5(timestamp));
        String queryStr = String.format(AUTH_URL_QUERY, userCode, sign1, sign2, timestamp);
        String authUrl = AUTH_URL + "?" + queryStr;

        HttpClientRequest request = authClient.get(authUrl, res -> {
            res.exceptionHandler(t -> {
                logger.error("auth response error.", t);
                stopwatch.stop();
                authClient.close();
            });

            res.bodyHandler(body -> {
                if (res.statusCode() == 200) {
                    String data = body.toString("utf-8");
                    logger.info("ge tui auth data=" + data);
                    JsonObject jsonObject = new JsonObject(data);
                    String code = jsonObject.getString("code");
                    if ("0".equals(code)) {
                        authToken = jsonObject.getString("access_token");
                        renewTokenFinished = true;
                    } else {
                        renewTokenFinished = false;
                        logger.error("ge tui auth error, code=" + code);
                    }
                    authClient.close();
                    stopwatch.stop();
                } else {
                    logger.error("ge tui auth error, statusCode=" + res.statusCode());
                    authClient.close();
                    stopwatch.stop();
                }
                logger.info("renewToken end, time=" + stopwatch + ", token = " + authToken);
            });
        });
        request.putHeader("Accept", "application/vnd.dmp.v1+json");
        request.putHeader("Content-Type", "application/json");
        request.setTimeout(10 * 1000); // 10s

        request.exceptionHandler(t -> {
            logger.error("auth request error.", t);
            authClient.close();
            stopwatch.stop();
        });

        request.end();
    }

    private static Map<String, String> getPostData(BidRequest bidRequest) {
        Map<String, String> data = Maps.newHashMap();
        OpenRtb.BidRequest openRtb = bidRequest.openRtb();
        OpenRtb.BidRequest.Device device = openRtb.getDevice();
        if (device.hasOs()) {
            data.put("os", device.getOs());
        }

        if (device.hasExtension(MexOpenRtbExt.imei)) {
            data.put("imeiMD5", MD5Utils.MD5(device.getExtension(MexOpenRtbExt.imei)));
        } else if (device.hasDidmd5()) {
            data.put("imeiMD5", device.getDidmd5());
        }

        if (device.hasExtension(MexOpenRtbExt.mac)) {
            data.put("macMD5", MD5Utils.MD5(device.getExtension(MexOpenRtbExt.mac)));
        } else if (device.hasMacmd5()) {
            data.put("macMD5", device.getMacmd5());
        }

        if (device.hasExtension(MexOpenRtbExt.androidId)) {
            data.put("androidIdMD5", MD5Utils.MD5(device.getExtension(MexOpenRtbExt.androidId)));
        } else if (device.hasDpidmd5()) {
            data.put("androidIdMD5", device.getDpidmd5());
        }

        if (device.hasExtension(MexOpenRtbExt.idfa)) {
            data.put("idfa", device.getExtension(MexOpenRtbExt.idfa));
            data.put("idfaMD5", MD5Utils.MD5(device.getExtension(MexOpenRtbExt.idfa)));
        }

        if (device.hasIp()) {
            data.put("ip", device.getIp());
        }

        //data.put("imeiMD5", "555a32ef3e9ee7e2585e78ac456a4829");
        return data;
    }

    private void renewQueryClient() {
        logger.info("renew query client");
        queryClient = vertx.createHttpClient(options);
    }

}

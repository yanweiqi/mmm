package com.mex.bidder.verticle;

import com.mex.bidder.api.http.ExchangeRouter;
import com.mex.bidder.api.vertx.guice.GuiceVerticle;
import com.mex.bidder.api.vertx.guice.GuiceVertxBinding;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.bizdata.MexDataContext;

import com.mex.bidder.engine.logger.ReqLogReceiver;

import com.mex.bidder.engine.metrics.BizDataHttpReceiver;
import com.mex.bidder.engine.metrics.MetricsHttpReceiver;
import com.mex.bidder.engine.metrics.PingHttpReceiver;
import com.mex.bidder.verticle.guice.BidderModule;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Bid请求接收入口
 * <p>
 * User: donghai
 * Date: 2016/11/14
 */
@GuiceVertxBinding(modules = {BidderModule.class})
public class BidderVerticle extends GuiceVerticle {
    private static final Logger logger = LoggerFactory.getLogger(BidderVerticle.class);

    public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_INSTANCE = 1;

    @Inject
    private PingHttpReceiver pingHttpReceiver;

    @Inject
    private BizDataHttpReceiver bizDataHttpReceiver;

    @Inject
    private ReqLogReceiver reqLogReceiver;

    @Inject
    private MetricsHttpReceiver metricsHttpReceiver;

    @Inject
    private ExchangeRouter exchangeRouter;

    @Inject
    private MexDataContext mexDataContext;

    @Inject
    @SysConf
    private JsonObject cnf;

    @Override
    public void onStart() {
        logger.info("start install BidderVerticle");
        mexDataContext.init(vertx);

        int bidderPort = getInt(cnf, "bidder-port", DEFAULT_PORT);
        logger.info("bidder listen port {}", bidderPort);

        HttpServerOptions serverOpts = new HttpServerOptions();
        serverOpts.setAcceptBacklog(65536);
        serverOpts.setTcpNoDelay(true);
        serverOpts.setTcpKeepAlive(true);
        HttpServer server = vertx.createHttpServer(serverOpts);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // favicon.ico
        router.get("/favicon.ico").handler(FaviconHandler.create());

        // ping检测
        router.get("/ping").handler(pingHttpReceiver);
        router.head("/ping").handler(pingHttpReceiver);
        logger.info("register ping url /ping");

        //metrics检测
        router.get("/metrics").handler(metricsHttpReceiver);
        logger.info("register monitoring url /metrics");

        // 查看服务当前业务数据
        router.get("/dt").handler(bizDataHttpReceiver);
        logger.info("register biz data url /dt");

        // 竞价处理
        router.post("/:channelName").handler(exchangeRouter);
        //router.get("/:channelName").handler(exchangeRouter);

        // 开启渠道请求日志
        router.get("/reqlog").handler(reqLogReceiver);
        logger.info("register reqlog data url /reqlog");




        router.route().failureHandler(ErrorHandler.create(true));
        server.requestHandler(router::accept).listen(bidderPort);

        logger.info("end install BidderVerticle,bidderPort=" + bidderPort);
    }

    static int getInt(JsonObject cnf, String cnfKey, int defaultVal) {
        if (cnf.containsKey(cnfKey)) {
            return cnf.getInteger(cnfKey);
        } else {
            return defaultVal;
        }
    }

}

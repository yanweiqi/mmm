package com.mex.bidder.verticle;

import com.mex.bidder.api.http.ExchangeRouter;
import com.mex.bidder.api.vertx.guice.GuiceVerticle;
import com.mex.bidder.api.vertx.guice.GuiceVertxBinding;
import com.mex.bidder.engine.bizdata.MexDataContext;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.redis.RedisClient;

import javax.inject.Inject;

/**
 * Bid请求接收入口
 * <p>
 * User: donghai
 * Date: 2016/11/14
 */
@GuiceVertxBinding(modules = {BidderApiTestModule.class})
public class TestBidderApi extends GuiceVerticle {
    public static final Logger logger = LoggerFactory.getLogger(TestBidderApi.class);


    @Inject
    private ExchangeRouter exchangeRouter;

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public void onStart() {
        logger.info("-------------------------logger--------------------------------");
        mexDataContext.init(vertx);


        RedisClient redis = RedisClient.create(vertx);
        redis.set("name", "donghai", res -> {

            System.out.println(TestBidderApi.class.getName() + " set " + res.succeeded());
        });


        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.get("/:channelName").handler(exchangeRouter);
        server.requestHandler(router::accept).listen(8080);

        // 接收DataVerticle的数据
        //EventBus eb = vertx.eventBus();
        // eb.consumer("mex.biz.creatives", message -> System.out.println("Received news on consumer 3: " + message.body()));
        logger.info("-------------------------logger end--------------------------------");

    }

}

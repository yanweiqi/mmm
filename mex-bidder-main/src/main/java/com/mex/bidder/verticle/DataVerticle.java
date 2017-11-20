package com.mex.bidder.verticle;

import com.mex.bidder.api.vertx.guice.GuiceVerticleHelper;
import com.mex.bidder.api.vertx.guice.GuiceVertxBinding;
import com.mex.bidder.config.PubSubRedis;
import com.mex.bidder.engine.bizdata.RedisMessageHandler;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.logger.LogMessageHandler;
import com.mex.bidder.engine.logger.LoggerContr;
import com.mex.bidder.verticle.guice.DataModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 加载数据的Verticle
 * <p>
 */
@GuiceVertxBinding(modules = DataModule.class)
public class DataVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(DataVerticle.class);

    @Inject
    @PubSubRedis
    private RedisClient redis;

    @Inject
    private RedisMessageHandler redisMessageHandler;

    private QpsContext qpsContext = new QpsContext();

    private int port =20008;


    @Override
    public void start() throws Exception {
        super.start();

        // 依赖注入
        GuiceVerticleHelper.inject(this, vertx);

        logger.info("----------install data verticle -------------");

        redis.psubscribe("rtb.channel.*", res -> {
            if (res.succeeded()) {
                logger.info("redis sub [rtb.*] channel ok");
            } else {
                logger.error("sub [rtb.*] error.", res.cause().fillInStackTrace());
            }
        });

        // 消费上面订阅的信息
        vertx.eventBus().consumer("io.vertx.redis.rtb.channel.*", (Message<JsonObject> msg) -> {
            JsonObject value = msg.body().getJsonObject("value");
            System.out.println("receive event -> " + value);
            redisMessageHandler.handle(value);
        });

        vertx.eventBus().consumer(MainVerticle.EB_SYNC_VERTICLE, event -> {
            logger.info("sync all data to bidder verticle");
            JsonObject value = new JsonObject();
            value.put("channel", "all");
            redisMessageHandler.handle(value);
        });

        vertx.eventBus().consumer(LoggerContr.EB_REQLOG_VERTICLE, event -> {
            logger.info("reqlog to bidder verticle");
            LogMessageHandler.handle();
        });

        vertx.eventBus().consumer(Constants.EB_QPS_VERTICLE, event -> {
            qpsContext.add();
        });


        HttpServerOptions serverOpts = new HttpServerOptions();
        serverOpts.setAcceptBacklog(65536);
        serverOpts.setTcpNoDelay(true);
        serverOpts.setTcpKeepAlive(true);
        serverOpts.setPort(port);
        HttpServer server = vertx.createHttpServer(serverOpts);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/qps").handler(routingContext ->{
            HttpServerResponse response = routingContext.response();
            response.end((qpsContext.get()+""));
        });

        server.requestHandler(router::accept).listen(port);

   /*     vertx.setPeriodic(2000,handler ->{
            logger.info("send msg---");
            vertx.eventBus().publish(Constants.EB_QPS_VERTICLE,"test11");
        });*/

        logger.info("init full data at start up");
        redisMessageHandler.initFullDataAtStartUp();

        logger.info("--------- install data verticle end ------------");
    }



    static class QpsContext {
        private AtomicLong qps = new AtomicLong();

        void add() {
            qps.incrementAndGet();
        }

        long get() {
            return qps.getAndSet(0);
        }

    }


}

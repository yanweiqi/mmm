package com.mex.bidder.verticle;

import com.mex.bidder.engine.kafka.producer.MessageProducer;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 启动程序，部署其他Verticle
 * <p>
 * User: donghai
 * Date: 2016/1/19
 */
public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    public static final String EB_SYNC_VERTICLE = "eb.sync.verticle";


    @Override
    public void start(Future<Void> future) {

        // 先读启动配置，如果启动没有指定配置，加载本地配置
        JsonObject config = config();
        if (config.isEmpty()) {
            logger.info("MainVerticle read from local conf");
            String confJson = JsonHelper.readFile("main.cnf.json");
            config = new JsonObject(confJson);
            checkConf(config);
        } else {
            logger.info("MainVerticle read from outside conf");
        }

        logger.info("MainVerticle -->" + config.toString());

        Integer bidderInstance = config.getInteger("bidder-instance");
        Integer kafkaPublisherInstance = config.getInteger("kafka-publisher-instance");

        vertx.deployVerticle(MessageProducer.class.getName(),
                new DeploymentOptions().setWorker(true).setInstances(kafkaPublisherInstance)
                        .setConfig(config), event -> {
                    logger.info("install MessageProducer ok,instanceCount=" + kafkaPublisherInstance);
                }
        );

        vertx.deployVerticle(DataVerticle.class.getName(),
                new DeploymentOptions().setWorker(true).setConfig(config), event -> logger.info("install DataVerticle ok")
        );

        vertx.deployVerticle(BidderVerticle.class.getName(),
                new DeploymentOptions().setInstances(bidderInstance).setConfig(config), event -> {
                    logger.info("install BidderVerticle ok, instanceCount=" + bidderInstance);
                    vertx.eventBus().publish(EB_SYNC_VERTICLE, "sync");
                }
        );


        vertx.setTimer(2000, re -> {
            // NOTE:确保同步数据， \
            // 所有业务数据在这步准备完成，增加数据同步时，应该在eb handle中添加处理
            // 参见 RedisMessageHandler#handle()方法
            vertx.eventBus().publish(EB_SYNC_VERTICLE, "sync");
        });

        logger.info(config.toString());

    }

    private static void checkConf(JsonObject config) {
        JsonObject configJsonObject = config.getJsonObject("message-redis");
        Objects.requireNonNull(configJsonObject, " message-redis can't be null");

        JsonObject queryRedisJson = config.getJsonObject("query-redis");
        Objects.requireNonNull(queryRedisJson, " query-redis can't be null");

        JsonObject kafkaCnf = config.getJsonObject("kafka-cnf");
        Objects.requireNonNull(kafkaCnf, " kafka-cnf can't be null");

        JsonObject newProducerTopics = config.getJsonObject("new-producer-topics");
        Objects.requireNonNull(newProducerTopics, " new-producer-topics can't be null");

        String ipLib = config.getString("ip-lib");
        Objects.requireNonNull(ipLib, " ip-lib can't be null");
    }
}


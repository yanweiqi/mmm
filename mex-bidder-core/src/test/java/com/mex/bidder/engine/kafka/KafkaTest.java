package com.mex.bidder.engine.kafka;

import com.google.inject.Inject;
import com.mex.bidder.engine.kafka.producer.KafkaPublisher;
import com.mex.bidder.engine.kafka.producer.MessageProducer;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * xuchuanao
 * on 2016/12/28.
 */
public class KafkaTest {

    Logger logger = LoggerFactory.getLogger(KafkaTest.class);

    KafkaPublisher kafkaPublisher;

    Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        kafkaPublisher = new KafkaPublisher(eventBus);
    }


    @Test
    public void sendMessage() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(MessageProducer.class.getName(), event -> {

            logger.info("install MessageProducer ok");
            kafkaPublisher.send("dspx_flow_report", "hello world");
        });

        latch.await(2000, TimeUnit.MILLISECONDS);
    }


}

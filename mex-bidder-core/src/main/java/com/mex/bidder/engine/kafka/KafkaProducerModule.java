package com.mex.bidder.engine.kafka;

import com.google.inject.AbstractModule;
import com.mex.bidder.engine.kafka.producer.KafkaPublisher;
import io.vertx.core.Vertx;

/**
 * kafka生产者
 * user: donghai
 * date: 2016/12/27
 */
public class KafkaProducerModule extends AbstractModule {
    private Vertx vertx;

    public KafkaProducerModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
        bind(KafkaPublisher.class).toInstance(newInstance(vertx));
    }

    private KafkaPublisher newInstance(Vertx vertx) {
        return new KafkaPublisher(vertx.eventBus());
    }
}

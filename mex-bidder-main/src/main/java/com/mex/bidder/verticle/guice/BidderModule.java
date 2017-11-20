package com.mex.bidder.verticle.guice;

import com.mex.bidder.api.vertx.guice.VertxModule;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.cnf.MainCnfModule;
import com.mex.bidder.engine.dmp.DmpModule;
import com.mex.bidder.engine.filter.FilterModule;
import com.mex.bidder.engine.interceptor.IntercepterModule;
import com.mex.bidder.engine.ip.IpModule;
import com.mex.bidder.engine.kafka.KafkaProducerModule;
import com.mex.bidder.engine.logger.LoggerModule;
import com.mex.bidder.engine.metrics.MetricsModule;
import com.mex.bidder.engine.ranking.RankingModule;
import com.mex.bidder.engine.redis.JedisModule;
import com.mex.bidder.engine.redis.RedisModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理竞价请求的依赖注入Module
 * <p>
 * User: donghai
 * Date: 2016/11/20
 */
public class BidderModule extends VertxModule {
    private static final Logger logger = LoggerFactory.getLogger(BidderModule.class);
    private Vertx vertx;

    @Override
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void configure() {
        // 读配置
        JsonObject config = vertx.getOrCreateContext().config();
        logger.info("BidderModule read conf  --- " + config);

        bind(JsonObject.class).annotatedWith(SysConf.class).toInstance(config);

        //install(new RedisModule(vertx));
        install(new MainCnfModule());
        install(new KafkaProducerModule(vertx));
        install(new IpModule(config));
        install(new JedisModule(config));
        install(new MetricsModule());
        install(new IntercepterModule());
        install(new FilterModule());
        install(new RankingModule());
        install(new LoggerModule());
        install(new AdxModule());
        install(new RedisModule(vertx, config));
        install(new DmpModule(vertx, config));

    }
}

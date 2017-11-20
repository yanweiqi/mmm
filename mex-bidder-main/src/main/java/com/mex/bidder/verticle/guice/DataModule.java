package com.mex.bidder.verticle.guice;

import com.mex.bidder.api.vertx.guice.VertxModule;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.cnf.MainCnfModule;
import com.mex.bidder.engine.redis.RedisModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理与投放业务网站的依赖注入
 * <p>
 * User: donghai
 * Date: 2016/11/20
 */
public class DataModule extends VertxModule {
    private static final Logger logger = LoggerFactory.getLogger(DataModule.class);
    private Vertx vertx;

    @Override
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
        // 读配置
        JsonObject config = vertx.getOrCreateContext().config();
        bind(JsonObject.class).annotatedWith(SysConf.class).toInstance(config);
        logger.info("DataModule read conf  --- " + config);


        install(new MainCnfModule());
        install(new RedisModule(vertx, config));
    }


}

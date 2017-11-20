package com.mex.bidder.engine.logger;

import com.google.inject.AbstractModule;
import com.mex.bidder.engine.logger.impl.KafkaLogger;
import com.mex.bidder.engine.logger.impl.Log4jLogger;

/**
 * 日志服务
 * User: donghai
 * Date: 2016/11/22
 */
public class LoggerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LoggerService.class).to(KafkaLogger.class).asEagerSingleton();
    }

}

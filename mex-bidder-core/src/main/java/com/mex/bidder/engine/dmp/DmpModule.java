package com.mex.bidder.engine.dmp;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.dmp.impl.GetuiDmpServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * DMP 模块定义
 * Created by cjw on 2017/6/16.
 */
public class DmpModule extends AbstractModule {
    private Vertx vertx;
    private JsonObject cnf;

    public DmpModule(Vertx vertx, @SysConf JsonObject config) {
        this.vertx = vertx;
        this.cnf = config;
    }

    @Override
    protected void configure() {
        JsonObject dmp = cnf.getJsonObject("dmp");

        if (dmp == null) {
            throw new RuntimeException("dmp cnf is empty");
        }
        JsonObject getui = dmp.getJsonObject("getui");
        if (getui == null) {
            throw new RuntimeException("dmp cnf is empty");
        }
        if (getui.getInteger("maxPoolSize") == null || getui.getInteger("maxPoolSize") <= 0) {
            getui.put("maxPoolSize", "20");
        }

        Preconditions.checkNotNull(getui.getString("host"), "host cant't be null");
        Preconditions.checkNotNull(getui.getInteger("port"), "port cant't be null");
        Preconditions.checkNotNull(getui.getInteger("timeout"), "timeout cant't be null");
        Preconditions.checkNotNull(getui.getString("userCode"), "userCode cant't be null");
        Preconditions.checkNotNull(getui.getString("authCode"), "userCode cant't be null");

        bind(GetuiDmpServiceImpl.class).toInstance(new GetuiDmpServiceImpl(vertx, getui));
    }
}

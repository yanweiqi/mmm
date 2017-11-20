package com.mex.bidder.engine.cnf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.util.JsonHelper;
import org.junit.Test;

import javax.inject.Inject;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class MainCnfModuleTest {
    @Inject
    @SysConf
    JSONObject cnf;

    @Test
    public void configure() throws Exception {
        String conf = JsonHelper.readFile("main.cnf.json");
        JSONObject jsonObject = JSON.parseObject(conf);

        System.out.println(jsonObject.getString("version"));
        System.out.println(jsonObject.getJSONObject("redis").getString("host"));
    }


    @Test
    public void testInject() {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new MainCnfModule());
            }
        });
        injector.injectMembers(this);

        System.out.println(cnf);
    }

}
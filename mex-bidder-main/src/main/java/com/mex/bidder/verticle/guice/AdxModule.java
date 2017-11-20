package com.mex.bidder.verticle.guice;

import com.google.inject.AbstractModule;
import com.mex.bidder.adx.adview.AdviewModule;
import com.mex.bidder.adx.gy.GyModule;
import com.mex.bidder.adx.baidu.BaiduModule;
import com.mex.bidder.adx.iflytek.IflytekModule;
import com.mex.bidder.adx.meitu.MeituModule;
import com.mex.bidder.adx.sohu.SohuModule;
import com.mex.bidder.adx.zplay.ZplayModule;

/**
 * Adx渠道module依赖注入集
 * 加一个Xxx渠道对应加一个XxxAdxModule。
 * <p>
 * User: donghai
 * Date: 2016/11/20
 */
public class AdxModule extends AbstractModule {
    @Override
    protected void configure() {

        install(new ZplayModule());
        install(new AdviewModule());
        install(new GyModule());
        install(new BaiduModule());
        install(new IflytekModule());
        install(new SohuModule());
        install(new MeituModule());
    }
}

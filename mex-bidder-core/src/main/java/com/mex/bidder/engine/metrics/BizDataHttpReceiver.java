package com.mex.bidder.engine.metrics;

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Maps;
import com.google.common.net.MediaType;
import com.mex.bidder.api.http.HttpReceiver;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.util.HttpUtil;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * User: donghai
 * Date: 2016/12/3
 */
public class BizDataHttpReceiver implements HttpReceiver, Handler<RoutingContext> {
    private static final Logger logger = LoggerFactory.getLogger(BizDataHttpReceiver.class);

    @Inject
    private MexDataContext mexDataContext;

    public static final String Key = "mex@123";

    @Override
    public void receive(RoutingContext ctx) {
        // 查看监控要密钥 TODO 配置化
        String secretKey = ctx.request().getParam("secret");
        if (!Key.equals(secretKey)) {
            HttpUtil.setStatusOk(ctx.response()).end("empty");
            return;
        }

        try {
            Map<String, Object> data = Maps.newHashMap();
//            data.put("adx-conf", mexDataContext.getAdxConfJson());
            data.put("adx-ad-groups", mexDataContext.getDspAdData());
//            data.put("adx-ad-status", mexDataContext.getAdStatusMap());
            data.put("adx-independent-dict", mexDataContext.getDspAdxDictData());
            data.put("adx-common-dict", mexDataContext.getDspCommonDictData());
            data.put("appTargeting-data",mexDataContext.getAppTargetingData());
            data.put("deviceIdTargeting-data",mexDataContext.getDeviceIdTargetingData());
            data.put("budgetPacing-data",mexDataContext.getBudgetPacingData());
            data.put("taTargeting-data",mexDataContext.getTaTargetingData());
            data.put("idPackageTargeting-data",mexDataContext.getTaIdPackageData());
            String JSONString = JSON.toJSONString(data);
            HttpUtil.setStatusOk(ctx.response());
            HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8).end(JSONString);
        } catch (Exception e) {
            logger.error("biz data error", e);
            HttpUtil.setStatusOk(ctx.response()).end("error");
        }
    }

    @Override
    public void handle(RoutingContext event) {
        receive(event);
    }
}

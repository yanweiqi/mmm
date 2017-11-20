package com.mex.bidder.engine.bizdata;

import com.alibaba.fastjson.JSON;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Map;

/**
 * Mex 业务数据上下文
 * <p>
 * User: donghai
 * Date: 2016/11/14
 */
@Singleton
public class MexDataContext {
    private static final Logger logger = LoggerFactory.getLogger(MexDataContext.class);

    private DspAdData dspAdData = new DspAdData();

    private DspAdxDictData dspAdxDictData;

    private DspCommonDictData dspCommonDictData;

    private DeviceIdTargetingData deviceIdTargetingData;

    // appanme定向数据
    private AppTargetingData appTargetingData;

    private BudgetPacingData budgetPacingData;

    private TaTargetingData taTargetingData;

    private TaIdPackageData taIdPackageData;

    private ReqLogData reqLogData = ReqLogData.getInstance();

    private AdPtrData adPtrData;


    //---------------------------------------------------------------------------------
    //                  通过vertx eb 接收数据初始数据 mex业务数据上下文
    //---------------------------------------------------------------------------------

    public void init(Vertx vertx) {
        logger.info("start MexDataContext init mex.biz.* eventbus consumer");

        vertx.eventBus().consumer(Const.EB_MEX_ADX_DATA, this::receiveDspAdxData);
        vertx.eventBus().consumer(Const.EB_MEX_INDEPENDENT_DICT_DATA, this::receiveDspIndependentDictData);
        vertx.eventBus().consumer(Const.EB_MEX_COMMON_DICT_DATA, this::receiveDspCommonDictData);
        vertx.eventBus().consumer(Const.EB_MEX_TARGETING_DEVICE_DATA, this::receiveDeviceTargetData);
        vertx.eventBus().consumer(Const.EB_REQLOG, this::receiveReqLogData);
        vertx.eventBus().consumer(Const.EB_MEX_TARGETING_APP_DATA, this::recevieAppNameTargetData);
        vertx.eventBus().consumer(Const.EB_MEX_BUDGET_PACING_APP_DATA, this::receiveBudgetPacingData);
        vertx.eventBus().consumer(Const.EB_MEX_TARGETING_TA_DATA, this::receiveTaTargetingData);
        vertx.eventBus().consumer(Const.EB_MEX_TARGETING_IDPACKAGE_DATA, this::receiveIdPackageData);
        vertx.eventBus().consumer(Const.EB_MEX_SMOOTH_DELIVERY_DATA, this::receiveAdPtrData);


        logger.info("end MexDataContext init mex.biz.* eventbus consumer");
    }

    private void receiveAdPtrData(Message<String> event) {
        if (event.body() == null) {
            adPtrData = AdPtrData.EMPTY;
        } else {
            String s = event.body().toString();
            logger.info("[mex.ad.ptr.data] receive msg -> " + s);
            adPtrData = JSON.parseObject(s, AdPtrData.class);
        }

    }

    private void receiveTaTargetingData(Message<String> event) {
        if (event.body() == null) {
            taTargetingData = TaTargetingData.EMPTY;
        } else {
            String s = event.body().toString();
            logger.info("[mex.ta.targeting.data] receive msg -> " + s);
            taTargetingData = JSON.parseObject(s, TaTargetingData.class);
        }
    }

    private void receiveIdPackageData(Message<String> event) {
        if (event.body() == null) {
            taIdPackageData = TaIdPackageData.EMPTY;
        } else {
            String s = event.body().toString();
            logger.info("[mex.idpackage.data] receive msg -> " + s);
            taIdPackageData = JSON.parseObject(s, TaIdPackageData.class);
        }
    }

    /**
     *
     */
    private void receiveBudgetPacingData(Message<String> event) {
        if (event.body() == null) {
            budgetPacingData = BudgetPacingData.EMPTY;
        } else {
            String s = event.body().toString();
            logger.info("[mex.budgetPacing.data] receive msg -> " + s);
            budgetPacingData = JSON.parseObject(s, BudgetPacingData.class);
        }

    }

    private void receiveReqLogData(Message<JsonObject> event) {
        if (event.body() == null) {
            reqLogData = ReqLogData.instance;
        } else {
            logger.info("[mex.reqlog.data] receive msg -> " + event.body().toString());
            reqLogData = JSON.parseObject(event.body().toString(), ReqLogData.class);
        }
    }


    /**
     * 接收dspAdData数据
     *
     * @param event
     */
    private void receiveDspAdxData(Message<Object> event) {
        if (event.body() == null) {
            dspAdData = DspAdData.NULL;
        } else {
            logger.info("[mex.adx.data] receive msg -> " + event.body().toString());
            dspAdData = JSON.parseObject(event.body().toString(), DspAdData.class);
            System.out.println("mex.adx.data === " + dspAdData.toString());
        }

    }

    private void receiveDspIndependentDictData(Message<Object> event) {
        if (event.body() == null) {
            dspAdxDictData = DspAdxDictData.NULL;
        } else {
            logger.info("[mex.adx.independent.dict.data] receive msg -> " + event.body().toString());
            dspAdxDictData = JSON.parseObject(event.body().toString(), DspAdxDictData.class);
            System.out.println("mex.adx.independent.dict.data ===" + dspAdData.toString());
        }

    }

    private void receiveDspCommonDictData(Message<Object> event) {
        if (event.body() == null) {
            dspCommonDictData = DspCommonDictData.NULL;
        } else {
            logger.info("[mex.adx.common.dict] receive msg -> " + event.body().toString());
            String s = event.body().toString();
            System.out.println("receive json ==== " + s);
            dspCommonDictData = JSON.parseObject(event.body().toString(), DspCommonDictData.class);
            System.out.println("mex.adx.common.dict ===" + dspAdData.toString());
        }
    }

    private void receiveDeviceTargetData(Message<Object> event) {
        logger.info("in receiveDeviceTargetData");
        if (event.body() == null) {  
            logger.info("DeviceTargetData is empty");
            deviceIdTargetingData = DeviceIdTargetingData.EMPTY;
        } else {
            logger.info("[mex.targeting.device.data] receive msg -> " + event.body().toString());
            String s = event.body().toString();
            deviceIdTargetingData = JSON.parseObject(s, DeviceIdTargetingData.class);
        }
    }

    private void recevieAppNameTargetData(Message<Object> event) {
        logger.info("in receiveAppNameTargetData");
        if (event.body() == null) {
            logger.info("AppNameTargetData is empty");
            appTargetingData = AppTargetingData.EMPTY;
        } else {
            logger.info("[mex.targeting.appname.data] receive msg -> " + event.body().toString());
            String s = event.body().toString();
            appTargetingData = JSON.parseObject(s, AppTargetingData.class);
            System.out.println("");
        }
    }

    //----------------------------------------------------------------------------------
    //                                 get method
    //----------------------------------------------------------------------------------

    public DspAdData getDspAdData() {
        return dspAdData;
    }

    public DspAdxDictData getDspAdxDictData() {
        return dspAdxDictData;
    }

    public DspCommonDictData getDspCommonDictData() {
        return dspCommonDictData;
    }

    public AdxData getAdxDataByExchange(Exchange exchange) {
        return dspAdData.getByExchangeId(exchange.getId());
    }

    public AdxDict getAdxDictByExchange(Exchange exchange) {
        return dspAdxDictData.get(exchange.getId());
    }

    public Map<String, String> getCommonDictByType(DictType dickType) {
        return dspCommonDictData.get(dickType);
    }

    public String getChannelFromReqLogData() {
        return reqLogData.getChannel();
    }

    public String getIsOpenFromReqLogData() {
        return reqLogData.getIsOpen();
    }

    public DeviceIdTargetingData getDeviceIdTargetingData() {
        return deviceIdTargetingData;
    }

    public AppTargetingData getAppTargetingData() {
        return appTargetingData;
    }

    public BudgetPacingData getBudgetPacingData() {
        return budgetPacingData;
    }

    public TaTargetingData getTaTargetingData() {
        return taTargetingData;
    }

    public void setTaTargetingData(TaTargetingData taTargetingData) {
        this.taTargetingData = taTargetingData;
    }

    public TaIdPackageData getTaIdPackageData() {
        return taIdPackageData;
    }

    public void setTaIdPackageData(TaIdPackageData taIdPackageData) {
        this.taIdPackageData = taIdPackageData;
    }

    public AdPtrData getAdPtrData() {
        return adPtrData;
    }


}

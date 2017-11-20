package com.mex.bidder.engine.bizdata;

import com.alibaba.fastjson.JSON;
import com.mex.bidder.config.Redis;
import com.mex.bidder.engine.interceptor.MexBidIntercepter;
import com.mex.bidder.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * redis 接收数据处理
 * User: donghai
 * Date: 2016/11/18
 */
@Singleton
public class RedisMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MexBidIntercepter.class);

    private final Vertx vertx;

    private final RedisClient redisClient;

//   private final ImmutableSet<Exchange> exchangeRoutes;

    @Inject
    public RedisMessageHandler(Vertx vertx, @Redis RedisClient redisClient) {
        this.vertx = vertx;
        this.redisClient = redisClient;
//        this.exchangeRoutes = ImmutableSet.copyOf(exchanges);
    }

    /**
     * 处理通过redis发布过来的事件
     *
     * @param event {pattern:"rtb.*",channel:"rtb.creative",message:"hi"}
     */
    public void handle(JsonObject event) {
        String channel = event.getString("channel");
        String message = event.getString("message");

        switch (channel) {
            case "all":
                initFullDataAtStartUp();
                break;
            case Const.REDIS_CHANNEL_KEY_AD_DATA:
                handleDspAdData();
                break;
            case Const.REDIS_CHANNEL_KEY_INDEPENDENT_DIC_DATA:
                handleDspAdxIndependentDictData();
                break;
            case Const.REDIS_CHANNEL_KEY_COMMON_DIC_DATA:
                handleDspAdxCommonDictData();
                break;
            case Const.REDIS_CHANNEL_KEY_TARGETING_DEVICE_DATA:
                handleDeviceIdTargetingData();
                break;
            case Const.REDIS_CHANNEL_KEY_TARGETING_APP_DATA:
                handleAppTargetingData();
                break;
            case Const.REDIS_CHANNEL_KEY_BUDGET_PACING_DATA:
                handleBudgetPacingData();
                break;
            case Const.REDIS_CHANNEL_KEY_TA_DATA:
                handleTaTargetingData();
                break;
            case Const.REDIS_CHANNEL_KEY_IDPACKAGE_DATA:
                handleIdPackageData();
                break;
            case Const.REDIS_CHANNEL_KEY_SMOOTH_DELIVERY_DATA:
                handleSmoothDeliveryData();
                break;
            default:
                logger.error("no channel accept, channel=" + channel);
        }
    }

    private void handleSmoothDeliveryData() {
        redisClient.get(Const.REDIS_DATA_KEY_SMOOTH_DELIVERY_DATA, res -> {
            if (res.succeeded()) {
                AdPtrData adPtrData = JSON.parseObject(res.result(), AdPtrData.class);
                if (Objects.isNull(adPtrData)) {
                    logger.info("AdPtrData size = 0");
                } else {
                    logger.info("AdPtrData :");
                    adPtrData.getData().forEach((k, v) -> {
                        logger.info("groupid=" + k + ", ptr=" + v);
                    });
                }

            } else {
                logger.info("AdPtrData error", res.cause().fillInStackTrace());
            }
            vertx.eventBus().publish(Const.EB_MEX_SMOOTH_DELIVERY_DATA, res.result());
        });
    }

    /**
     * 处理设备包数据
     */
    private void handleIdPackageData() {
        redisClient.get(Const.REDIS_DATA_KEY_IDPACKAGE_DATA, res -> {
            if (res.succeeded()) {
                TaIdPackageData taIdPackageData = JSON.parseObject(res.result(), TaIdPackageData.class);
                if (taIdPackageData.getDeviceIdToData().size() == 0) {
                    logger.info("taIdPackageData deviceIdToData size = 0");
                } else {
                    taIdPackageData.getDeviceIdToData().keySet().forEach(k -> {
                        logger.info("deviceIdToData key =" + k + ", val=" + taIdPackageData.getDeviceIdToData().get(k));
                    });
                }

                if (taIdPackageData.getGroupTaSetMap().size() == 0) {
                    logger.info("taIdPackageData groupTaSetMap size = 0");
                } else {
                    taIdPackageData.getGroupTaSetMap().keySet().forEach(k -> {
                        logger.info("deviceIdToData key =" + k + ", val=" + taIdPackageData.getGroupTaSetMap().get(k));
                    });
                }
            } else {
                logger.error("taIdPackageData error", res.cause().fillInStackTrace());
            }

            vertx.eventBus().publish(Const.EB_MEX_TARGETING_IDPACKAGE_DATA, res.result());
        });
    }

    /**
     * 处理Ta数据
     */
    private void handleTaTargetingData() {
        redisClient.get(Const.REDIS_DATA_KEY_TA_DATA, res -> {
            if (res.succeeded()) {
                TaTargetingData taTargetingData = JSON.parseObject(res.result(), TaTargetingData.class);
                if (taTargetingData.getData().size() == 0) {
                    logger.info("taTargetingData size = 0");
                } else {
                    taTargetingData.getData().keySet().forEach(k -> {
                        logger.info("taTargetingData key=" + k + ", val=" + taTargetingData.getData().get(k));
                    });
                    vertx.eventBus().publish(Const.EB_MEX_TARGETING_TA_DATA, res.result());
                }
            } else {
                logger.error("taTargetingData error", res.cause().fillInStackTrace());
            }
        });
    }

    /**
     * 处理匀速投放数据
     */
    private void handleBudgetPacingData() {
        redisClient.get(Const.REDIS_DATA_KEY_BUDGET_PACING_DATA, res -> {
            if (res.succeeded()) {
                BudgetPacingData budgetPacingData = JSON.parseObject(res.result(), BudgetPacingData.class);
                if (Objects.isNull(budgetPacingData.getData())) {
                    logger.info("budgetPacingData size = 0");
                } else {
                    budgetPacingData.getData().keySet().forEach(k -> {
                        logger.info("budgetPacingData key=" + k + ", val=" + budgetPacingData.getData().get(k));
                    });
                    vertx.eventBus().publish(Const.EB_MEX_BUDGET_PACING_APP_DATA, res.result());
                }
            } else {
                logger.error("handleBudgetPacingData error", res.cause().fillInStackTrace());
            }
        });
    }

    /**
     * adx业务数据
     */
    void handleDspAdData() {
        redisClient.get(Const.REDIS_DATA_KEY_AD_DATA, res -> {
            if (res.succeeded()) {
                DspAdData dspAdData = JSON.parseObject(res.result(), DspAdData.class);
                Map<String, AdxData> exchangeData = dspAdData.getExchangeData();
                logger.info("===========================adxData==============================");
                exchangeData.forEach((s, adxData) -> {
                    Map<String, List<Banner>> bannerMap = adxData.getBannerMap();
                    logger.info("exchangeId = [" + s + "], dataSize = [" + bannerMap.size() + "]");
                    bannerMap.forEach((size, BannerList) -> {
                        logger.info("size = [" + size + "]");
                    });
                });
                vertx.eventBus().publish(Const.EB_MEX_ADX_DATA, res.result());
            } else {
                logger.error("handleDspAdData error", res.cause().fillInStackTrace());
            }
        });
    }

    /**
     * 独立的字典数据
     */
    void handleDspAdxIndependentDictData() {
        redisClient.get(Const.REDIS_DATA_KEY_INDEPENDENT_DIC_DATA, res -> {
            if (res.succeeded()) {
                vertx.eventBus().publish(Const.EB_MEX_INDEPENDENT_DICT_DATA, res.result());
            } else {
                logger.error("handleDspAdxIndependentDictData error", res.cause().fillInStackTrace());
            }
        });
    }

    /**
     * 共同的字典数据
     */
    void handleDspAdxCommonDictData() {
        redisClient.get(Const.REDIS_DATA_KEY_COMMON_DIC_DATA, res -> {
            if (res.succeeded()) {
                vertx.eventBus().publish(Const.EB_MEX_COMMON_DICT_DATA, res.result());
            } else {
                logger.error("handleDspAdxCommonDictData error", res.cause().fillInStackTrace());
            }
        });
    }

    /**
     * 处理设备定向
     */
    private void handleDeviceIdTargetingData() {
        logger.info("in handleDeviceIdTargetingData");
        redisClient.get(Const.REDIS_DATA_KEY_TARGETING_DEVICE_DATA, res -> {
            logger.info("handleDeviceIdTargetingData callback");
            if (res.succeeded()) {
                logger.info("handleDeviceIdTargetingData succeeded");
                vertx.eventBus().publish(Const.EB_MEX_TARGETING_DEVICE_DATA, res.result());
            } else {
                logger.error("handleDeviceIdTargetingData error", res.cause().fillInStackTrace());
            }
        });
    }

    /**
     * 处理app定向
     */
    private void handleAppTargetingData() {
        logger.info("in handleAppTargetingData");
        {
            redisClient.get(Const.REDIS_DATA_KEY_TARGETING_APP_DATA, res -> {
                logger.info("handleAppTargetingData callback");
                if (res.succeeded()) {
                    logger.info("handleAppTargetingData succeeded");
                    vertx.eventBus().publish(Const.EB_MEX_TARGETING_APP_DATA, res.result());
                } else {
                    logger.error("handleAppTargetingData error", res.cause().fillInStackTrace());
                }
            });
        }
    }

    /**
     * 首次启动时，主动从redis同步业务数据
     */
    public void initFullDataAtStartUp() {
        //加载业务数据
        handleDspAdData();

        handleDspAdxIndependentDictData();

        handleDspAdxCommonDictData();

        handleDeviceIdTargetingData();

        handleAppTargetingData();

        handleBudgetPacingData();

        handleIdPackageData();

        handleTaTargetingData();

        handleSmoothDeliveryData();

    }


}

package com.mex.bidder.engine.interceptor;

import com.google.common.base.Stopwatch;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidInterceptor;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorChain;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.MexBidderEngine;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.ip.IpService;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.AdxData;
import com.mex.bidder.protocol.Banner;
import com.mex.bidder.protocol.nativead.Native;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * mex bidder 核心业务拦截器
 * <p>
 * User: donghai
 * Date: 2016/11/16
 */
public class MexBidIntercepter implements BidInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MexBidIntercepter.class);

    @Inject
    private MexDataContext mexDataContext;
    @Inject
    private MexBidderEngine mexBidderEngine;
    @Inject
    private IpService ipService;

    @Override
    public void execute(InterceptorChain<BidRequest, BidResponse> chain) {

        BidRequest request = chain.request();
        BidResponse response = chain.response();
        Exchange exchange = request.getExchange();
        OpenRtb.BidRequest openRtbBidRequest = request.openRtb();

        // 获取渠道对应的广告集
        AdxData adMaterialGroup = mexDataContext.getAdxDataByExchange(exchange);


        // ip 地址查询
        request.setIpBean(ipService.lookup(RtbHelper.getIp(openRtbBidRequest)));
        logger.info("in MexBidIntercepter ......................................");
        if (RtbHelper.hasNative(openRtbBidRequest)) {
            if (adMaterialGroup.getNativeList().isEmpty()) {
                response.addFilterError("0-0-0-0", FilterErrors.NATIVE_FAIL_EMPTY);
                chain.resultHandler().handle(Future.succeededFuture());
                return;
            }

            handleNative(adMaterialGroup, request, response, res -> {
                if (res.succeeded()) {
                    response.setAdAndPricePair(res.result());
                    response.setResponseMode(res.result() == AdAndPricePair.EMPTY ?
                            BidResponse.ResponseMode.NONE : BidResponse.ResponseMode.NATIVE);
                    // 链后面的interceptor逻辑
                    // chain.proceed();
                    chain.resultHandler().handle(Future.succeededFuture());

                } else {
                    logger.error("handle native error.", res.cause());
                    chain.resultHandler().handle(Future.failedFuture(res.cause()));
                }
            });

        } else if (RtbHelper.hasBanner(openRtbBidRequest)) {
            // Banner 广告
            if (adMaterialGroup.getBannerMap().isEmpty()) {
                response.addFilterError("0-0-0-0", FilterErrors.FILTER_FAIL_EMPTY);
                chain.resultHandler().handle(Future.succeededFuture());
                return;
            }
            // 通过过滤器筛选出投放的广告
            handleBanner(adMaterialGroup, request, response, res -> {
                logger.info(" in-------------------------------------------------- handleBanner callback");
                if (res.succeeded()) {
                    response.setAdAndPricePair(res.result());
                    response.setResponseMode(res.result() == AdAndPricePair.EMPTY ?
                            BidResponse.ResponseMode.NONE : BidResponse.ResponseMode.BANNER);
                    // 链后面的interceptor逻辑
                    // chain.proceed();
                    logger.info("out ------------------------------------------ handleBanner callback, ---" + response.getResponseMode());
                    chain.resultHandler().handle(Future.succeededFuture());
                } else {
                    logger.error("handle banner error.", res.cause());
                    chain.resultHandler().handle(Future.failedFuture(res.cause()));
                }

            });
        } else if (RtbHelper.hasVideo(openRtbBidRequest)) {
            // TODO: 2016/11/19
            logger.info("{} video request", request.getExchange().getId());
            handleVideo(adMaterialGroup, request, response);
            // 链后面的interceptor逻辑
            chain.proceed();
        } else {
            logger.info("{} other ad request", request.getExchange().getId());
        }
        logger.info("out MexBidIntercepter ......................................");

    }

    private void handleNative(AdxData adMaterialGroup, BidRequest request, BidResponse response,
                              Handler<AsyncResult<AdAndPricePair>> handler) {
        logger.info("in handleNative ......................................");
        List<Native> nativeList = adMaterialGroup.getNativeList();
        if (nativeList.isEmpty()) {
            handler.handle(Future.succeededFuture(AdAndPricePair.EMPTY));
        } else {
            mexBidderEngine.bidding(nativeList, request, response).setHandler(handler);
        }
        logger.info("out handleNative ......................................");
    }

    private AdAndPricePair handleVideo(AdxData adMaterialGroup, BidRequest request, BidResponse response) {
        OpenRtb.BidRequest.Imp.Video video = request.openRtb().getImp(0).getVideo();
        String size = RtbHelper.getVideoSize(video);

        return null;
    }


    /**
     * 处理 Banner广告
     *
     * @param adMaterialGroup
     * @param bidRequest
     * @return
     */
    protected void handleBanner(AdxData adMaterialGroup, BidRequest bidRequest, BidResponse bidResponse,
                                Handler<AsyncResult<AdAndPricePair>> handler) {
        OpenRtb.BidRequest.Imp.Banner banner = bidRequest.openRtb().getImp(0).getBanner();

        // 按广告位尺寸索引
        String bannerSize = RtbHelper.getBannerSize(banner);
        List<Banner> candidatesBannerList = adMaterialGroup.getBannerListBySize(bannerSize);
        if (null == candidatesBannerList || candidatesBannerList.isEmpty()) {
            logger.warn("exchangeId {} no ad found under size {} ", bidRequest.getExchange().getId(), bannerSize);
            // 渠道没有匹配的广告尺寸，记录错误日志
            logger.info("{} no ad with size {} found", bidRequest.getExchange().getId(), bannerSize);
            Stopwatch stopwatch = Stopwatch.createStarted();
            adMaterialGroup.getBannerMap().forEach((k, v) -> {
                v.forEach(ad -> bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_Wh));
            });
            stopwatch.stop();
            logger.info("record filter failed log, time={}", stopwatch.toString());
            handler.handle(Future.succeededFuture(AdAndPricePair.EMPTY));
        } else {
            // 正常竞价  调用过滤器链
            mexBidderEngine.bidding(candidatesBannerList, bidRequest, bidResponse).setHandler(handler);
        }
    }

}

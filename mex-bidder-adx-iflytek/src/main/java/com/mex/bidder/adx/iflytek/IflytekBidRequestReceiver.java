package com.mex.bidder.adx.iflytek;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.net.MediaType;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorAbortException;
import com.mex.bidder.api.interceptor.RequestReceiver;
import com.mex.bidder.api.mapper.MapperException;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.logger.LoggerService;
import com.mex.bidder.engine.util.HttpUtil;
import com.mex.bidder.util.Clock;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class IflytekBidRequestReceiver extends RequestReceiver<BidController> {
    private static final Logger logger = LoggerFactory.getLogger(IflytekBidRequestReceiver.class);

    private final Meter successResponseWithAdsMeter;
    private final Meter successResponseNoAdsMeter;

    private final LoggerService loggerService;
    private final IflytekOpenRtbMapper mapper;
    private final Clock clock;
    private final IflytekMacroProcessor macroProcessor;

    @Inject
    public IflytekBidRequestReceiver(
            MetricRegistry metricsRegistry,
            LoggerService loggerService,
            IflytekOpenRtbMapper mapper,
            IflytekMacroProcessor macroProcessor,
            BidController controller,
            Clock clock) {
        super(IflytekExchange.INSTANCE, metricsRegistry, controller);

        this.loggerService = loggerService;
        this.mapper = mapper;
        this.clock = clock;
        this.macroProcessor = macroProcessor;
        this.successResponseNoAdsMeter = buildMeter("success-response-no-ads");
        this.successResponseWithAdsMeter = buildMeter("success-response-with-ads");
    }

    @Override
    public void receive(RoutingContext ctx) {
        boolean unhandledException = false;
        Timer.Context timerContext = requestTimer().time();

        try {
            long start = clock.nanoTime();
            BidRequest request = newRequest(ctx);
            BidResponse response = newResponse(ctx).build();
            setRequestId(request.openRtb().getId());
            Future<AsyncResult<Void>> handleBidRequestFutures = handleBidRequest(request, response);
            handleBidRequestFutures.setHandler(getAsyncResultHandler(ctx, start, request, response));
        } catch (InvalidProtocolBufferException e) {
            logger.error("Bad iflytek request: {}", e);
            ctx.response().setStatusCode(400).end();
        } catch (Exception e) {
            logger.error("error ", e);
            ctx.response().setStatusCode(500).end();
        } finally {
            if (unhandledException) {
                interceptorOtherMeter().mark();
            }
            timerContext.close();
        }
    }

    private Future<AsyncResult<Void>> handleBidRequest(BidRequest request, BidResponse response) {
        Future<AsyncResult<Void>> result = Future.future();
        try {
            // 业务处理
            controller().onRequest(request, response, rs -> {
                result.complete(Future.succeededFuture());
            });
        } catch (InterceptorAbortException e) {
            logger.error("InterceptorAbortException thrown", e);
            interceptorAbortMeter().mark();
            result.complete(Future.failedFuture(e));
        } catch (MapperException e) {
            logger.error(e.toString());
            result.complete(Future.failedFuture(e));
        }

        return result;
    }

    private Handler<AsyncResult<AsyncResult<Void>>> getAsyncResultHandler(RoutingContext ctx, long start, BidRequest request, BidResponse response) {
        Handler<AsyncResult<AsyncResult<Void>>> handler = res -> {
            if (res.succeeded()) {

                OpenRtb.BidRequest openrtbRequest = request.openRtb();
                OpenRtb.BidResponse.Builder openrtbResponse = response.openRtb();
                if (!openrtbResponse.hasId()) {
                    openrtbResponse.setId(openrtbRequest.getId());
                }

                // 宏处理
                macroProcessor.process(request, response);

                // 映射到科大的相应对象
                String iflytekResponse = mapper.toExchangeBidResponse(openrtbRequest, openrtbResponse.build());

                (Strings.isNullOrEmpty(iflytekResponse)
                        ? successResponseNoAdsMeter
                        : successResponseWithAdsMeter
                ).mark();
                successResponseMeter().mark();


                // Step 4. 处理响应
                if (response.getResponseMode() == BidResponse.ResponseMode.NONE) {
                    HttpUtil.setStatusCode(ctx.response(), 204);
                    ctx.response().end();
                } else {
                    HttpUtil.setStatusOk(ctx.response());
                    HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8);
                    ctx.response().end(iflytekResponse);
                }

                long end = clock.nanoTime();
                int processTime = (int) ((end - start) / 1000000);

                //Step 5. 记录响应日志
                loggerService.sendRequestLog(request, response);
                requestLog(request);
                loggerService.sendResponseLog(response, processTime, request);
                responseLog(response, processTime);
                responseRawLog(iflytekResponse, IflytekExchange.INSTANCE);
                logger.info("ProcessingTimeMs " + processTime + " ms");
            } else {
                logger.error("iflytek error ", res.cause());
                ctx.response().setStatusCode(500).end();
            }
            clearRequestId();
        };

        return handler;
    }


    private BidRequest newRequest(RoutingContext ctx) throws InvalidProtocolBufferException {

        @Nullable Buffer body = ctx.getBody();

        if (body == null) {
            throw new InvalidProtocolBufferException("iflytek parse bid request error");
        }
        String requestData = new String(body.getBytes());

        requestRawLog(requestData, IflytekExchange.INSTANCE);

        OpenRtb.BidRequest.Builder builder = mapper.toOpenRtbBidRequest(requestData);
        OpenRtb.BidRequest.Device.Builder deviceBuilder = builder.getDeviceBuilder();
        String os = deviceBuilder.getOs();

        // readDevice的时候同时设置了idfa和Androidid的值，需要根据os来情况清空某一个值
        if ("android".equals(os)) {
            deviceBuilder.setExtension(MexOpenRtbExt.idfa, "");
        } else {
            deviceBuilder.setExtension(MexOpenRtbExt.androidId, "");
        }

        //统一协议的特殊处理逻辑
        if (builder.getImpCount() > 0) {
            OpenRtb.BidRequest.Imp imp = builder.getImp(0);
            Integer iflytekInstl = imp.getExtension(MexOpenRtbExt.iflytekInstl);

            //   instl = 7  一图一文 图片通过img传
            //   instl = 8  一图两文 图片通过img传
            //   instl = 12 一图    图片通过img_urls传
            //   instl = 13 三图一文 图片通过img_urls传，但科大在协议里只传一个img对象，
            //   为统一后面处理流程，在解释协议后，加上两个img对象。
            if (Objects.nonNull(iflytekInstl) && iflytekInstl == 13) {
                Optional<OpenRtb.NativeRequest.Asset> image = imp.getNative()
                        .getRequestNative().getAssetsList().stream()
                        .filter(OpenRtb.NativeRequest.Asset::hasImg).findFirst();
                if (image.isPresent()) {
                    builder.getImpBuilder(0).getNativeBuilder().getRequestNativeBuilder().addAssets(image.get());
                    builder.getImpBuilder(0).getNativeBuilder().getRequestNativeBuilder().addAssets(image.get());
                }
            }
        }

        return BidRequest.newBuilder()
                .setHttpRequest(ctx.request())
                .setExchange(getExchange())
                .setNativeRequest(requestData)
                .setRequest(builder).build();
    }


}

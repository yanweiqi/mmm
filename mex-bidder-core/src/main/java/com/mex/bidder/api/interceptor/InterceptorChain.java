
package com.mex.bidder.api.interceptor;

import com.codahale.metrics.Timer;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.mex.bidder.api.platform.CompatibleExchanges;
import com.mex.bidder.api.platform.Exchange;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 参考Servlet FilterChain
 *
 * @param <Req>  The request type for this chain
 * @param <Resp> The response type for this chain
 */
public class InterceptorChain<Req extends UserRequest, Resp extends UserResponse<Resp>> {
    private static final Logger logger = LoggerFactory.getLogger(InterceptorChain.class);

    private final InterceptorController<Req, Resp> controller;
    private final Req request;
    private final Resp response;
    private final Iterator<? extends Interceptor<Req, Resp>> interceptors;
    private Handler<AsyncResult<Void>> resultHandler;

    protected InterceptorChain(InterceptorController<Req, Resp> controller,
                               Req request, Resp response,Handler<AsyncResult<Void>> resultHandler) {
        this.request = checkNotNull(request);
        this.response = checkNotNull(response);
        this.controller = checkNotNull(controller);
        this.interceptors = controller.getInterceptors().iterator();
        this.resultHandler = resultHandler;
    }

    public static <Req extends UserRequest, Resp extends UserResponse<Resp>>
    InterceptorChain<Req, Resp> create(
            InterceptorController<Req, Resp> controller, Req request, Resp response, Handler<AsyncResult<Void>>next) {
        return new InterceptorChain<>(controller, request, response,next);
    }

    private static boolean isCompatible(Interceptor<?, ?> interceptor, Exchange exchange) {
        CompatibleExchanges compat = interceptor.getClass().getAnnotation(CompatibleExchanges.class);
        if (compat == null) {
            return true;
        }
        for (String exchangeName : compat.value()) {
            if (exchangeName.equals(exchange.getId())) {
                return true;
            }
        }
        return false;
    }

    public final void proceed() {
        boolean foundCompatible = false;

        while (!foundCompatible && interceptors.hasNext()) {
            Interceptor<Req, Resp> interceptor = interceptors.next();

            if (isCompatible(interceptor, request().getExchange())) {
                foundCompatible = true;
                if (logger.isTraceEnabled()) {
                    logger.trace(">> Interceptor: {}", interceptor.getClass().getSimpleName());
                }

                Timer timer = controller.getResource(Timer.class, interceptor);
                Timer.Context timerContext = timer == null ? null : timer.time();

                call(interceptor);

                if (logger.isTraceEnabled()) {
                    logger.trace("<< Interceptor: {}", interceptor.getClass().getSimpleName());
                }

                if (timerContext != null) {
                    timerContext.close();
                }
            } else if (logger.isTraceEnabled()) {
                logger.trace("Ignoring interceptor: {}", interceptor.getClass().getSimpleName());
            }
        }
    }

    protected <I extends Interceptor<Req, Resp>> void call(I interceptor) {
        interceptor.execute(this);
    }

    public final Req request() {
        return request;
    }

    public final Resp response() {
        return response;
    }

    public final Handler<AsyncResult<Void>>  resultHandler(){return resultHandler;}

    public Iterator<? extends Interceptor<Req, Resp>> nextInterceptors() {
        return interceptors;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("request", request)
                .add("response", response);
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }
}

package com.mex.bidder.api.interceptor;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mex.bidder.util.ReflectionUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class StandardInterceptorController<Req extends UserRequest, Resp extends UserResponse<Resp>>
        implements InterceptorController<Req, Resp> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ImmutableList<? extends Interceptor<Req, Resp>> interceptors;
    private final ImmutableMap<Interceptor<Req, Resp>, Timer> interceptorTimers;

    public StandardInterceptorController(List<? extends Interceptor<Req, Resp>> interceptors,
                                         MetricRegistry metricRegistry) {
        this.interceptors = ImmutableList.copyOf(interceptors);
        logger.info("Interceptors: {}",
                Lists.transform(this.interceptors, ReflectionUtils.TO_CLASSNAME));

        ImmutableMap.Builder<Interceptor<Req, Resp>, Timer> interceptorTimers = ImmutableMap.builder();
        for (Interceptor<Req, Resp> interceptor : getInterceptors()) {
            interceptorTimers.put(interceptor, metricRegistry.register(
                    MetricRegistry.name(interceptor.getClass(), "execute"), new Timer()));
        }
        this.interceptorTimers = interceptorTimers.build();

        final ImmutableList<String> interceptorNames = ImmutableList.copyOf(Lists.transform(
                this.interceptors, new Function<Interceptor<Req, Resp>, String>() {
                    @Override
                    public String apply(Interceptor<Req, Resp> interceptor) {
                        return ReflectionUtils.TO_PRETTYCLASSNAME.apply(interceptor);
                    }
                }));

        metricRegistry.register(MetricRegistry.name(getClass(), "interceptors"),
                new Gauge<List<String>>() {
                    @Override
                    public List<String> getValue() {
                        return interceptorNames;
                    }
                });
    }

    @Override
    public
    @Nullable
    <R> R getResource(Class<R> resourceType, Interceptor<Req, Resp> interceptor) {
        checkNotNull(resourceType);
        checkNotNull(interceptor);

        if (resourceType == Timer.class) {
            @SuppressWarnings("unchecked")
            R resource = (R) interceptorTimers.get(interceptor);
            return resource;
        } else {
            return null;
        }
    }

    @Override
    public void onRequest(Req request, Resp response, Handler<AsyncResult<Void>> next) {
        InterceptorChain<Req, Resp> interceptorChain = InterceptorChain.create(this, request, response,next);
        interceptorChain.proceed();
    }

    @Override
    public ImmutableList<? extends Interceptor<Req, Resp>> getInterceptors() {
        return interceptors;
    }


    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("interceptors", Lists.transform(interceptors, ReflectionUtils.TO_SIMPLECLASSNAME));
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }
}

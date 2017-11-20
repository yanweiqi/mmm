package com.mex.bidder.api.interceptor;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mex.bidder.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

public abstract class CompositeInterceptor<Req extends UserRequest, Resp extends UserResponse<Resp>>
        implements Interceptor<Req, Resp> {

    private final ImmutableList<? extends Interceptor<Req, Resp>> componentInterceptors;
    private final MetricRegistry metricRegistry;

    protected CompositeInterceptor(List<? extends Interceptor<Req, Resp>> componentInterceptors,
                                   MetricRegistry metricRegistry) {
        this.componentInterceptors = ImmutableList.copyOf(componentInterceptors);
        this.metricRegistry = metricRegistry;
    }

    public ImmutableList<? extends Interceptor<Req, Resp>> getComponentInterceptors() {
        return componentInterceptors;
    }

    @Override
    public void execute(final InterceptorChain<Req, Resp> chain) {
        final InterceptorChain<Req, Resp> componentChain = InterceptorChain.create(
                new CompositeController<>(componentInterceptors, metricRegistry),
                chain.request(), chain.response(), chain.resultHandler());
        componentChain.proceed();
        chain.proceed();
    }

    @PostConstruct
    public void postConstruct() {
        ReflectionUtils.invokePostConstruct(componentInterceptors);
    }

    @PreDestroy
    public void preDestroy() {
        ReflectionUtils.invokePreDestroy(componentInterceptors);
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("componentInterceptors",
                        Lists.transform(componentInterceptors, ReflectionUtils.TO_SIMPLECLASSNAME));
    }

    private static final class CompositeController
            <Req extends UserRequest, Resp extends UserResponse<Resp>>
            extends StandardInterceptorController<Req, Resp> {
        private CompositeController(
                List<? extends Interceptor<Req, Resp>> interceptors, MetricRegistry metricRegistry) {
            super(interceptors, metricRegistry);
        }
    }
}

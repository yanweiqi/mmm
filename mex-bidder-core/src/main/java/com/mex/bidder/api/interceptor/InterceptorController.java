package com.mex.bidder.api.interceptor;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.annotation.Nullable;
import java.util.List;


public interface InterceptorController<Req extends UserRequest, Resp extends UserResponse<Resp>> {


    void onRequest(Req request, Resp response, Handler<AsyncResult<Void>> next);


    List<? extends Interceptor<Req, Resp>> getInterceptors();


    @Nullable
    <R> R getResource(Class<R> resourceType, Interceptor<Req, Resp> interceptor);
}

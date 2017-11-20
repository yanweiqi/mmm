package com.mex.bidder.api.interceptor;

import javax.inject.Singleton;

/**
 * Root interceptor interface.
 * <p>
 */
@Singleton
public interface Interceptor<Req extends UserRequest, Resp extends UserResponse<Resp>> {


    void execute(InterceptorChain<Req, Resp> chain);
}

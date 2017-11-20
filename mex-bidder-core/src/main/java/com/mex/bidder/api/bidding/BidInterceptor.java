package com.mex.bidder.api.bidding;


import com.mex.bidder.api.interceptor.Interceptor;

/**
 * {@link BidRequest} 请求的拦截器. 业务逻辑在拦截器中实现
 */
public interface BidInterceptor extends Interceptor<BidRequest, BidResponse> {
}

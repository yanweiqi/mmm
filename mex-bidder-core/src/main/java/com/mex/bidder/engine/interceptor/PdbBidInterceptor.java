package com.mex.bidder.engine.interceptor;

import com.mex.bidder.api.bidding.BidInterceptor;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorChain;

/**
 * pdb 投放拦截器
 * <p>
 * pdb要求，不管定向过滤后是否存在，都应该下发一个与广告位同尺寸的广告。
 * 因此，如果定向过滤后没有匹配的广告，通过渠道配置项，
 * User: donghai
 * Date: 2016/11/18
 */
public class PdbBidInterceptor implements BidInterceptor {
    // @Inject
    //private MexDataContext mexDataContext;

    @Override
    public void execute(InterceptorChain<BidRequest, BidResponse> chain) {
        chain.proceed();

        // TODO
    }
}

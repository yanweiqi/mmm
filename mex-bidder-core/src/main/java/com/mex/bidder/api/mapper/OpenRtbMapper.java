package com.mex.bidder.api.mapper;

import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidResponse;

import javax.annotation.Nullable;

/**
 * Converts between OpenRTB and exchange-specific requests/response.
 * <p>
 * Implementations of this interface have to be threadsafe.
 *
 * @param <ReqIn>   Type for the exchange-specific bid request model (input)
 * @param <RespIn>  Type for the exchange-specific bid response model (input)
 * @param <ReqOut>  Type for the exchange-specific bid request model (output)
 * @param <RespOut> Type for the exchange-specific bid response model (output)
 */
public interface OpenRtbMapper<ReqIn, RespIn, ReqOut, RespOut> {

    /**
     * Converts an OpenRTB response to the exchange-specific format.
     *
     * @param request  OpenRTB request, if necessary for context or validations
     * @param response OpenRTB response
     * @return Response in the exchange-specific format
     */
    RespOut toExchangeBidResponse(@Nullable BidRequest request, BidResponse response);

    /**
     * Converts an exchange-specific request to OpenRTB.
     *
     * @param request Request in the exchange-specific format
     * @return OpenRTB request
     */
    BidRequest.Builder toOpenRtbBidRequest(ReqIn request);

    /**
     * Converts an OpenRTB request to the exchange-specific format.
     *
     * @param request OpenRTB request
     * @return Request in the exchange-specific format
     */
    // ReqOut toExchangeBidRequest(@Nullable BidRequest request);

    /**
     * Converts a n exchange-specific response to OpenRTB.
     *
     * @param request  Request in the exchange-specific format, if necessary for context or validations
     * @param response The response
     * @return OpenRTB response
     */
    // BidResponse.Builder toOpenRtbBidResponse(@Nullable ReqIn request, RespIn response);
}

package com.mex.bidder.engine.dmp;

import com.mex.bidder.api.bidding.BidRequest;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * 对接DMP
 * <p>
 * date: 2017/6/14
 */
public interface DmpService {

    Future<Optional<JsonObject>> retrieve(BidRequest bidRequest);
}

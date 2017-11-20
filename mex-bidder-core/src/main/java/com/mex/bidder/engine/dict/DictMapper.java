package com.mex.bidder.engine.dict;

/**
 * User: donghai
 * Date: 2016/11/21
 */
public interface DictMapper<IN,OUT> {
    OUT toMex(IN input);
}

package com.mex.bidder.api.mapper;

/**
 * User: donghai
 * Date: 2016/11/20
 */
public class MapperException extends RuntimeException {

    public MapperException(String format, Object... args) {
        super(String.format(format, args));
    }
}

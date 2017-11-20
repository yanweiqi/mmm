package com.mex.bidder.engine;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class ParseException extends RuntimeException {

    public ParseException(final String description) {
        super(description);
    }


    public ParseException(String description, Throwable cause) {
        super(description, cause);
    }
}

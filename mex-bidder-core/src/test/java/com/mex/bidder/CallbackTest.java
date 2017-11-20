package com.mex.bidder;

/**
 * User: donghai
 * Date: 2016/11/25
 */
public class CallbackTest {
    public static void main(String[] args) {

        CallbackTest callbackTest = new CallbackTest();

        Mycallback mycallback = result -> {
            System.out.println("in callback print ==> " + result);
            callbackTest.afterCallback();
        };

        callbackTest.process("hello", "world", mycallback);

    }

    public void process(String a, String b, Mycallback mycallback) {
        System.out.println("process a and b");
        mycallback.invoke(a + " - " + b);
        System.out.println("---------------");
    }

    private void afterCallback() {
        System.out.println(" after callback out");
    }

    static interface Mycallback {
        void invoke(String result);
    }
}

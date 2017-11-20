package com.mex.bidder.concurent;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

/**
 * xuchuahao
 * on 2017/7/5.
 */
public class AtomicTest {

    private final AtomicLong count = new AtomicLong();

    private int k = 0;

    public long getCount() {
        return count.get();
    }
    public long getCount2() {
        return k;
    }

    public void countingDemo() {
        System.out.println("start...");
        count.getAndIncrement();
        System.out.println("end...");
    }

    public void countingDemo2() {
        k++;
    }

    Runnable runnable = () -> {
        countingDemo2();
        long count1 = getCount2();
        System.out.println("count=" + count1);
    };

    @Test
    public void caculateDemo() throws InterruptedException {
        new Thread(runnable).start();
        new Thread(runnable).start();
        for (int i  = 0 ; i < 100;i++){
            new Thread(runnable).start();
        }
        Thread.sleep(2000l);

    }


}

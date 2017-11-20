package com.mex.bidder.concurent;

/**
 * xuchuahao
 * on 2017/7/5.
 */
public class LockDemo implements Runnable {
    @Override
    public void run() {
        get();
    }

    public synchronized void get() {
        System.out.println(Thread.currentThread().getId());
        set();
    }

    public synchronized void set() {
        System.out.println(Thread.currentThread().getId());
    }

    public static void main(String[] args) {
        LockDemo ld = new LockDemo();
        new Thread(ld).start();
        new Thread(ld).start();
        new Thread(ld).start();
        new Thread(ld).start();
    }
}

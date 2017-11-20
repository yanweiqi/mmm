package com.mex.bidder.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * xuchuanao
 * on 2016/12/29.
 */
public class RandomAdxMock {

    public static void main(String[] args) throws Exception {

        AdviewAdxMock adviewAdxMock = new AdviewAdxMock();
        BaiduAdxMock baiduAdxMock = new BaiduAdxMock();
        GyAdxMock gyAdxMock = new GyAdxMock();
        IflytekMock iflytekMock = new IflytekMock();
        ZpalyAdxMock zpalyAdxMock = new ZpalyAdxMock();

        int cnt = Integer.parseInt(args[1]);
        if (args[0].equals("iflytek")) {
            doRun(iflytekMock::run, cnt);
        } else if (args[0].equals("adszp")) {
            doRun(gyAdxMock::run, cnt);
        } else if (args[0].equals("adsview")) {
            doRun(adviewAdxMock::run, cnt);
        } else if (args[0].equals("adsgy")) {
            doRun(gyAdxMock::run, cnt);
        } else if (args[0].equals("adsmbd")) {
            doRun(baiduAdxMock::run, cnt);
        } else if (args[0].equals("random")){
            RandomSend(cnt,adviewAdxMock, iflytekMock, zpalyAdxMock);
        }

        Thread.currentThread().join();
    }


    private static void doRun(Runnable r, int cnt) {
        try {
            int i = cnt;
            for (int k = 0; k < i; k++) {
                r.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RandomSend(int cnt,AdviewAdxMock adviewAdxMock, IflytekMock iflytekMock, ZpalyAdxMock zpalyAdxMock){
        for (int i = 0; i < cnt; i++) {
            Random random = new Random(System.currentTimeMillis());
            int i1 = random.nextInt(4);

            switch (i1){
                case 0:
                    adviewAdxMock.run();
                    break;
                case 1:
                    adviewAdxMock.run();
                    break;
                case 2:
                    iflytekMock.run();
                    break;
                case 3:
                    iflytekMock.run();
                    break;
                case 4:
                    zpalyAdxMock.run();
                    break;
                default:
                    break;
            }

        }
    }

}

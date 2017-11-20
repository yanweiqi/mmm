package com.mex.bidder.adx.adview;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

/**
 * xuchuahao
 * on 2017/5/16.
 */
public class AppName {

    @Test
    public void appCtr1() {
        File file = new File("E:\\16-adview\\new-data.txt");
        File to = new File("E:\\16-adview\\ctr.txt");
        try {
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            for (String line : lines) {
                String[] split = line.split("\t");
                String name = split[3];
                String impNum = split[4];
                String clkNum = split[5].equals("#N/A") ? "0" : split[5];
                String ctr = split[6].equals("#N/A") ? "0" : split[6];

                name = URLDecoder.decode(name, "utf-8");
                Files.append(name + ", " + impNum + ", " + clkNum + ", " + ctr + "\n", to, Charsets.UTF_8);
                System.out.println("name=" + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void appCtr() {
        File file = new File("E:\\adview\\zz-test-app.txt");
        File to = new File("E:\\adview\\ctr.txt");
        try {
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            for (String line : lines) {
                String[] split = line.split("\t");
                String name = split[7];
                String impNum = split[8];
                String clkNum = split[9].equals("#N/A") ? "0" : split[9];
                String ctr = split[10].equals("#N/A") ? "0" : split[10];

                name = URLDecoder.decode(name, "utf-8");
                Files.append(name + ", " + impNum + ", " + clkNum + ", " + ctr + "\n", to, Charsets.UTF_8);
                System.out.println("name=" + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void app() {
        File file = new File("E:\\adview\\z-adview-app-num.txt");
        File to = new File("E:\\adview-to.txt");
        try {
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            for (String line : lines) {
                String[] split = line.split(" ");
                String name = split[0];
                String num = split[1];
                name = URLDecoder.decode(name, "utf-8");
                Files.append(name + " " + num + "\n", to, Charsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

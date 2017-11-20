package com.imei;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * user: donghai
 * date: 2017/7/31
 */
public class TestImeiLuhn {

    public static boolean check(String ccNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static void main(String[] args) {
        String fileName = "D:\\z26-uniq.txt";

        Pattern pattern = Pattern.compile("^[0-9]*$");
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.filter(line -> line.length() == 15).forEach(line -> {
                try {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches() && check(line)) {

                    } else {
                        System.out.println("illegal imei " + line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(check("861929039470206"));
    }
}

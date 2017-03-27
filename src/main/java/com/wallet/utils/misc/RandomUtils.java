package com.wallet.utils.misc;

import java.security.SecureRandom;

/**
 * Created by zxqiu on 3/27/17.
 */
public class RandomUtils {
    public static String genString(int bytes) {
        StringBuilder buf = new StringBuilder();
        SecureRandom sRandom = new SecureRandom();

        for (int i = 0; i < bytes; i++) {
            boolean upper = sRandom.nextBoolean();
            char ch = (char)(sRandom.nextInt(26) + 'a');

            if (upper) {
                ch=Character.toUpperCase(ch);
            }

            buf.append(ch);
        }

        return buf.toString();
    }
}

package com.wallet.utils.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zxqiu on 4/16/17.
 */
public class Hashing {
    public static String MD5Hash(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes());

        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}

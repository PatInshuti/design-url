package com.system_design_url.starter.utils;

public class Utils {
    public static final int length = 15;
    public static final String base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String localHost = "127.0.0.1";

    public static enum bloomFilter{
        LONG_URL,
        SHORT_URL
    }
}


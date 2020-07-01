package io.github.jzdayz.utils;

public class AESArg {
    public final static String PWD = System.getProperty("key","key_value_length");

    static {
        if (PWD.length()!=16){
            throw new RuntimeException("key length must be 16");
        }
    }
}

package com.example.tsngapp.helpers;

public class StringUtil {
    public static String capitalize(String str) {
        if (str == null) return "";
        if (str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

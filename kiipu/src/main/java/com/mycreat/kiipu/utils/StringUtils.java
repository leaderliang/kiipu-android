package com.mycreat.kiipu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangyanqiao on 2017/3/29.
 */
public class StringUtils {
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    public static String capitalize(final String word) {
        if (word.length() > 1) {
            return String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1);
        }
        return word;
    }

    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.equals("") || s.equals("null");
    }

    public static String dealWithEmptyStr(CharSequence c) {
        return c == null || c.equals("") || c.equals("null") ? "" : c.toString();
    }

}

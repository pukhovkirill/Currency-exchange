package org.cnyex.utils;

public class Utils {
    public static String getCodeFromUri(String uri){
        var parts = uri.split("/");
        var len = parts.length;
        return parts[len-1];
    }
}

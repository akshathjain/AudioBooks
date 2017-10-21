package com.akshathjain.bookworm.utils;

/**
 * Created by Akshath on 10/20/2017.
 */

public class TimeConverter {
    public static String format(int tSec){
        String toReturn = "";
        int s = tSec % 60;
        int m = ((tSec - s) / 60) % 60;
        int h = (tSec - 60 * m - s) / 3600;

        String ss = (s < 10 ? "0" : "") + s;
        String ms = (h > 0 && m < 10 ? "0" : "") + m;
        String hs = (h < 10 ? "0" : "") + h;

        return (h == 0 ? "" : (hs + ":")) + ms + ":" + ss;
    }
}

package com.akshathjain.bookworm.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Akshath on 10/19/2017.
 */

public class Web {
    public static String getWebData(String url) {
        //set up a Http request, connect, get input stream, parse, and return
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream());

            String result = "";
            while (scanner.hasNext())
                result += scanner.nextLine();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

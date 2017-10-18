package com.akshathjain.bookworm;

/*
Name: Akshath Jain
Date: 10/18/17
Purpose: generic audio book class
 */

import org.json.JSONObject;

public class AudioBook {
    private JSONObject bookReference;
    private String title;
    private String author;

    public AudioBook(JSONObject o){
        bookReference = o;
    }
}

package com.akshathjain.bookworm;

/*
Name: Akshath Jain
Date: 10/18/17
Purpose: generic audio book class
 */

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class AudioBook {
    private JSONObject bookReference;
    private String title;
    private String author;
    private String baseURL;
    private String thumbnailURL;

    private ArrayList<Chapter> chapterList;

    public AudioBook(String title){
        this.title = title;
        chapterList = new ArrayList<>();
    }

    public void addChapter(String title, String chapterURL, String track, String runtime){
        chapterList.add(new Chapter(title, chapterURL, track, runtime));
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public String toString() {
        String toReturn = "Title: " + title + "\n";
        for(Chapter c : getChapterList()){
            toReturn += c.toString() + "\n";
        }
        return toReturn;
    }

    public ArrayList<Chapter> getChapterList() {
        Collections.sort(chapterList);
        return chapterList;
    }
}

class Chapter implements Comparable<Chapter>{
    private String title;
    private String url;
    private int track;
    private double runtime;

    public Chapter(String title, String url, String track, String runtime){
        this.title = title;
        this.url = url;
        this.track = Integer.parseInt(track.split("/")[0]);
        this.runtime = Double.parseDouble(runtime);
    }

    public Chapter(String title, String url, String track, double runtime){
        this.title = title;
        this.url = url;
        this.track = Integer.parseInt(track.split("/")[0]);
        this.runtime = runtime;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public double getRuntime() {
        return runtime;
    }

    @Override
    public String toString() {
        return "Chapter: " + title + " " + "URL: " + " " + url;
    }

    @Override
    public int compareTo(Chapter other) {
        return this.track - other.track;
    }
}

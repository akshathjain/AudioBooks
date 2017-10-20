package com.akshathjain.bookworm;

/*
Name: Akshath Jain
Date: 10/18/17
Purpose: generic audio book class
 */

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class AudioBook implements Serializable {
    private String title;
    private String author;
    private String description;
    private String chaptersURL;
    private String thumbnailURL;
    private ArrayList<Chapter> chapterList;
    private int currentChapter = 0;
    private boolean isSorted = false; //flag to indicate whether or not the chpater list needs sorting, set to false when a new element is added

    public AudioBook() {
        chapterList = new ArrayList<>();
    }

    public AudioBook(String title) {
        this.title = title;
        chapterList = new ArrayList<>();
    }

    public ArrayList<Chapter> getChapterList() {
        if(!isSorted) {
            Collections.sort(chapterList);
            isSorted = true;
        }
        return chapterList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setChaptersURL(String chaptersURL) {
        this.chaptersURL = chaptersURL;
    }

    public void addChapter(String title, String chapterURL, String track, String runtime) {
        chapterList.add(new Chapter(title, chapterURL, track, runtime));
        isSorted = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getChaptersURL() {
        return chaptersURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    @Override
    public String toString() {
        String toReturn = "Title: " + title + "\n";
        for (Chapter c : getChapterList()) {
            toReturn += c.toString() + "\n";
        }
        return toReturn;
    }

    //get previous chapter if in bounds
    public Chapter getPreviousChapter(){
        if(currentChapter > 0) {
            currentChapter--;
            return getChapterList().get(currentChapter);
        }else
            return null;
    }

    //method to get the url for the current chapter
    public Chapter getCurrentChapter(){
        return getChapterList().get(currentChapter);
    }

    //get next chapter if in bounds
    public Chapter getNextChapter(){
        if(currentChapter < chapterList.size() - 1){
            currentChapter++;
            return getChapterList().get(currentChapter);
        }else
            return null;
    }

    //get chapter if in bounds
    public Chapter getChapter(int number){
        if(number >= 0 && number <= chapterList.size() - 1){
            currentChapter = number;
            return getChapterList().get(currentChapter);
        }else
            return null;
    }

}

class Chapter implements Comparable<Chapter>, Serializable {
    private String title;
    private String url;
    private int track;
    private double runtime;

    public Chapter(String title, String url, String track, String runtime) {
        this.title = title;
        this.url = url;
        this.track = Integer.parseInt(track.split("/")[0]);
        this.runtime = Double.parseDouble(runtime);
    }

    public Chapter(String title, String url, String track, double runtime) {
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

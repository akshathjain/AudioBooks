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

    public AudioBook() {
        chapterList = new ArrayList<>();
    }

    public AudioBook(String title) {
        this.title = title;
        chapterList = new ArrayList<>();
    }

    public ArrayList<Chapter> getChapterList() {
        Collections.sort(chapterList);
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
}

class Chapter implements Comparable<Chapter> {
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

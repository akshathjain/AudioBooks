package com.akshathjain.bookworm.generic;

import java.io.Serializable;

/**
 * Created by Akshath on 10/20/2017.
 */

public class Chapter implements Comparable<Chapter>, Serializable {
    private String title;
    private String url;
    private int track;
    private double runtime;

    public Chapter(String title, String url, String track, String runtime) {
        this.title = title;
        setUrl(url);
        this.track = Integer.parseInt(track.split("/")[0]);
        this.runtime = Double.parseDouble(runtime);
    }

    public Chapter(String title, String url, String track, double runtime) {
        this.title = title;
        setUrl(url);
        this.track = Integer.parseInt(track.split("/")[0]);
        this.runtime = runtime;
    }

    public void setUrl(String url) {
        if(!url.contains("http://"))
            url = "http://" + url;
        this.url = url;
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

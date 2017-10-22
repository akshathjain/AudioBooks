package com.akshathjain.bookworm.generic;

/*
Name: Akshath Jain
Date: 10/18/17
Purpose: generic audio book class
 */

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

    public String[] getChapterNames(){
        String[] toReturn = new String[getChapterList().size()];
        for(int i = 0; i < toReturn.length; i++)
            toReturn[i] = getChapterList().get(i).getTitle();
        return toReturn;
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
        if(hasPreviousChapter()) {
            currentChapter--;
            return getChapterList().get(currentChapter);
        }else
            return null;
    }

    public boolean hasPreviousChapter(){
        return currentChapter > 0;
    }

    //method to get the url for the current chapter
    public Chapter getCurrentChapter(){
        return getChapterList().get(currentChapter);
    }

    //get next chapter if in bounds
    public Chapter getNextChapter(){
        if(hasNextChapter()){
            currentChapter++;
            return getChapterList().get(currentChapter);
        }else
            return null;
    }

    public boolean hasNextChapter(){
        return currentChapter < chapterList.size() - 1;
    }

    //get chapter if in bounds
    public Chapter getChapter(int number){
        if(hasChapter(number)){
            currentChapter = number;
            return getChapterList().get(currentChapter);
        }else
            return null;
    }

    public boolean hasChapter(int number){
        return number >= 0 && number <= chapterList.size() - 1;
    }
}

package com.akshathjain.bookworm.async;

import android.os.AsyncTask;

import com.akshathjain.bookworm.AudioBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by Akshath on 10/18/2017.
 */

public class QueryRetriever extends AsyncTask<String, Void, ArrayList<AudioBook>> {
    private QueryFinished callback;

    @Override
    protected ArrayList<AudioBook> doInBackground(String... searchTerm) {
        try {
            ArrayList<AudioBook> books = new ArrayList<>();

            JSONObject librivox = new JSONObject(getWebData(searchTerm[0])).getJSONObject("books");
            Iterator<String> keys = librivox.keys();
            while (keys.hasNext()) {
                JSONObject bookData = librivox.getJSONObject(keys.next());
                AudioBook currentBook = new AudioBook();

                //set title
                currentBook.setTitle(bookData.optString("title"));

                //set authors
                currentBook.setAuthor(bookData.optJSONArray("authors").optJSONObject(0).optString("first_name") + " " + bookData.optJSONArray("authors").optJSONObject(0).optString("last_name"));

                //set chapters url and thumbnail url
                String iarchive = bookData.optString("url_iarchive").substring(bookData.optString("url_iarchive").lastIndexOf("/"));
                currentBook.setChaptersURL("http://archive.org/metadata/" + iarchive + "/files");
                currentBook.setThumbnailURL("https://archive.org/services/img/"+ iarchive);

                /*JSONArray iarchiveData = new JSONObject(getWebData(baseURL)).getJSONArray("result");
                for(int i = 0; i < iarchiveData.length(); i++){
                    JSONObject current = iarchiveData.getJSONObject(i);
                    if(current.getString("source").equals("original") && current.getString("format").toLowerCase().contains("mp3")){
                        currentBook.addChapter(current.getString("title"), "https://archive.org/details/" + urlIdentifier + "/" + current.getString("name"), current.getString("track"),current.getString("length"));
                    }else if(current.getString("format").toLowerCase().contains("thumb")){
                        currentBook.setThumbnailURL("https://archive.org/details/" + urlIdentifier + "/" + current.getString("name"));
                    }
                }*/

                books.add(currentBook);
            }

            return books;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(ArrayList<AudioBook> result) {
        super.onPostExecute(result);
        callback.onQueryFinished(result);
    }

    public void addOnCompleted(QueryFinished callback) {
        this.callback = callback;
    }

    private String getWebData(String url) {
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


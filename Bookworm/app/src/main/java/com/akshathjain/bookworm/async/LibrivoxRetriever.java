package com.akshathjain.bookworm.async;

import android.os.AsyncTask;

import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.utils.Web;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Akshath on 10/18/2017.
 */

public class LibrivoxRetriever extends AsyncTask<String, Void, ArrayList<AudioBook>> {
    private QueryFinished callback;

    @Override
    protected ArrayList<AudioBook> doInBackground(String... searchTerm) {
        try {
            ArrayList<AudioBook> books = new ArrayList<>();

            JSONObject librivox = new JSONObject(Web.getWebData(searchTerm[0])).getJSONObject("books");
            Iterator<String> keys = librivox.keys();
            while (keys.hasNext()) {
                JSONObject bookData = librivox.getJSONObject(keys.next());
                AudioBook currentBook = new AudioBook();

                //set title
                currentBook.setTitle(bookData.optString("title"));

                //set authors
                currentBook.setAuthor(bookData.optJSONArray("authors").optJSONObject(0).optString("first_name") + " " + bookData.optJSONArray("authors").optJSONObject(0).optString("last_name"));

                //set chapters url and thumbnail url
                String iarchive = bookData.optString("url_iarchive").substring(1 + bookData.optString("url_iarchive").lastIndexOf("/"));
                currentBook.setChaptersURL("http://archive.org/metadata/" + iarchive);
                currentBook.setThumbnailURL("https://archive.org/services/img/"+ iarchive);

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
}


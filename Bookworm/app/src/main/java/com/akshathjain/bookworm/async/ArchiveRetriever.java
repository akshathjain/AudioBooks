package com.akshathjain.bookworm.async;

import android.os.AsyncTask;

import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.utils.Web;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Name: Akshath Jain
 * Date: 10/19/2017
 * Purpose: internet archive async thread to obtain chapter urls
 */

public class ArchiveRetriever extends AsyncTask<String, Void, Void> {
    private AudioBook reference;
    private QueryFinished callback;

    public ArchiveRetriever(AudioBook book) {
        super();
        this.reference = book;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            JSONObject iarchiveData = new JSONObject(Web.getWebData(strings[0]));
            String base = iarchiveData.optString("d1") + iarchiveData.optString("dir");

            JSONArray files = iarchiveData.optJSONArray("files");
            for (int i = 0; i < iarchiveData.length(); i++) {
                JSONObject current = files.getJSONObject(i);
                if (current.optString("source").equals("original") && current.optString("format").toLowerCase().contains("mp3")) {
                    reference.addChapter(current.optString("title"), base + "/" + current.optString("name"), current.optString("track"), current.optString("length"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        callback.onQueryFinished(aVoid);
    }

    public void addOnCompleted(QueryFinished callback) {
        this.callback = callback;
    }
}

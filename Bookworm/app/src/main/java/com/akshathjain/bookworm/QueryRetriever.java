package com.akshathjain.bookworm;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Akshath on 10/18/2017.
 */

public class QueryRetriever extends AsyncTask<String, Void, JSONObject> {
    private QueryFinished callback;

    @Override
    protected JSONObject doInBackground(String... baseURL) {
        String result = null;

        try{
            //set up a Http request
            URL url = new URL(baseURL[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream());

            result = new String();
            while(scanner.hasNext())
                result += scanner.nextLine();

            return new JSONObject(result);
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        callback.onQueryFinished(result);
    }

    public void addOnCompleted(QueryFinished callback){
        this.callback = callback;
    }
}

interface QueryFinished<T>{
    void onQueryFinished(T t);
}


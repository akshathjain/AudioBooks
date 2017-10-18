package com.akshathjain.bookworm;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        /*android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        AudioPlayer frag = new AudioPlayer();
        fragmentTransaction.add(R.id.fragment_container, frag);
        fragmentTransaction.commit();*/

    }

    public void openPlayer(View view) {
        QueryRetriever retriever = new QueryRetriever();
        retriever.addOnCompleted(new QueryFinished<JSONObject>() {
            @Override
            public void onQueryFinished(JSONObject o) {
                System.out.println(o);
            }
        });
        retriever.execute("https://librivox.org/api/feed/audiobooks/?title=pride%20and%20prejudice&format=json");
    }
}

class QueryRetriever extends AsyncTask<String, Void, JSONObject>{
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


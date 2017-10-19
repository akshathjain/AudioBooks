package com.akshathjain.bookworm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
    }

    public void openPlayer(View view) {
        QueryRetriever retriever = new QueryRetriever();
        retriever.addOnCompleted(new QueryFinished<JSONObject[]>() {
            @Override
            public void onQueryFinished(JSONObject[] o) {
               /* try {
                    System.out.println(o[0].toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                AudioPlayer frag = new AudioPlayer();
                fragmentTransaction.add(R.id.fragment_container, frag);
                fragmentTransaction.commit();
            }
        });
        retriever.execute("https://librivox.org/api/feed/audiobooks/?title=pride%20and%20prejudice&format=json&extended=1");
    }
}

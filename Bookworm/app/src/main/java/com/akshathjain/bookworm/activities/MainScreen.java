package com.akshathjain.bookworm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.fragments.Player;
import com.akshathjain.bookworm.R;
import com.akshathjain.bookworm.async.QueryFinished;
import com.akshathjain.bookworm.async.LibrivoxRetriever;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        openPlayer();
    }

    public void openPlayer() {
        LibrivoxRetriever retriever = new LibrivoxRetriever();
        retriever.addOnCompleted(new QueryFinished<ArrayList<AudioBook>>() {
            @Override
            public void onQueryFinished(ArrayList<AudioBook> o) {
                android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Player frag = new Player();
                Bundle b = new Bundle();
                b.putSerializable("AUDIO_BOOK", o.get(0));
                frag.setArguments(b);

                fragmentTransaction.add(R.id.fragment_container, frag);
                fragmentTransaction.commit();
            }
        });
        retriever.execute("https://librivox.org/api/feed/audiobooks/?title=pride%20and%20prejudice&format=json&extended=1");
    }


}

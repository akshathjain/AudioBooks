package com.akshathjain.bookworm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.fragments.Player;
import com.akshathjain.bookworm.R;
import com.akshathjain.bookworm.interfaces.LayoutFinished;
import com.akshathjain.bookworm.interfaces.QueryFinished;
import com.akshathjain.bookworm.async.LibrivoxRetriever;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {
    private SlidingUpPanelLayout slidePanel;
    private FrameLayout musicPlayerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        openPlayer();

        slidePanel = findViewById(R.id.sliding_panel);
        musicPlayerContainer = findViewById(R.id.player_container);
        musicPlayerContainer.setVisibility(View.INVISIBLE); //set invisible until music is loaded
        slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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

                fragmentTransaction.add(R.id.player_container, frag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                frag.addOnLayoutFinished(new LayoutFinished() {
                    @Override
                    public void onLayoutFinished(View v) {
                        slidePanel.setDragView(v);
                    }
                });

                frag.addOnMusicLoaded(new Player.MusicLoaded() {
                    @Override
                    public void onMusicLoaded() {
                        slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                });
            }
        });
        retriever.execute("https://librivox.org/api/feed/audiobooks/?title=^pride%20and%20prejudice&format=json&extended=1");
    }

}


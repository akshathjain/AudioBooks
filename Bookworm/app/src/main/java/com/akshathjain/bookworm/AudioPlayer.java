package com.akshathjain.bookworm;

/*
Name: Akshath Jain
Date: 10/17/17
Purpose: Audio player fragment
 */

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

public class AudioPlayer extends Fragment {
    private ImageView playPause;
    private ImageView trackNext;
    private ImageView trackPrevious;
    private SeekBar seekBar;
    private boolean isMusicPlaying = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_audio_player, container, false);

        playPause = (ImageView) layout.findViewById(R.id.track_play_pause);
        trackNext = (ImageView) layout.findViewById(R.id.track_next);
        trackPrevious = (ImageView) layout.findViewById(R.id.track_previous);
        seekBar = (SeekBar) layout.findViewById(R.id.track_seekbar);

        setupOnClickListeners();

        return layout;
    }

    //function to set up on click listeners
    private void setupOnClickListeners(){
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseMusic();
            }
        });
    }

    //function to start playing music
    private void playPauseMusic(){
        isMusicPlaying = !isMusicPlaying;
        if(isMusicPlaying)
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);
        else
            playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

}

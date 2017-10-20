package com.akshathjain.bookworm;

/*
Name: Akshath Jain
Date: 10/17/17
Purpose: Audio player fragment
 */

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.akshathjain.bookworm.async.ArchiveRetriever;
import com.akshathjain.bookworm.async.QueryFinished;
import com.bumptech.glide.Glide;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Player extends Fragment {
    private AudioBook book;
    private ImageView playPause;
    private ImageView trackNext;
    private ImageView trackPrevious;
    private SeekBar seekBar;
    private TextView title;
    private TextView chapter;
    private ImageView thumbnail;
    private boolean isMusicPlaying = false;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_audio_player, container, false);

        playPause = layout.findViewById(R.id.track_play_pause);
        trackNext = layout.findViewById(R.id.track_next);
        trackPrevious = layout.findViewById(R.id.track_previous);
        seekBar = layout.findViewById(R.id.track_seekbar);
        title = layout.findViewById(R.id.track_title);
        chapter = layout.findViewById(R.id.track_chapter);
        thumbnail = layout.findViewById(R.id.track_thumbnail);

        //get the book information and then get the chapter information
        Bundle args = getArguments();
        this.book = (AudioBook) args.getSerializable("AUDIO_BOOK");
        ArchiveRetriever streamGetter = new ArchiveRetriever(book);
        streamGetter.addOnCompleted(new QueryFinished<Void>() {
            @Override
            public void onQueryFinished(Void aVoid) {
                setupMediaPlayer(book.getCurrentChapter().getUrl());
            }
        });
        streamGetter.execute(book.getChaptersURL());

        //set book information
        title.setText(book.getTitle()); //bind title
        Glide.with(this).load(book.getThumbnailURL()).transition(withCrossFade()).into(thumbnail); //bind thumbnail

        setupOnClickListeners();

        return layout;
    }

    //function to set up on click listeners
    private void setupOnClickListeners() {
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseMusic();
            }
        });
    }

    //function to setup mediaplayer / wakelock functionality
    private void setupMediaPlayer(String url) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource("http://ia802602.us.archive.org/9/items/pride_and_prejudice_librivox/prideandprejudice_01-03_austen_64kb.mp3");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playPauseMusic();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //function to start playing music
    private void playPauseMusic() {
        isMusicPlaying = !isMusicPlaying;
        if (isMusicPlaying) {
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);
            mediaPlayer.start();
        } else {
            playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mediaPlayer.pause();
        }
    }

}

package com.akshathjain.bookworm.fragments;

/*
Name: Akshath Jain
Date: 10/17/17
Purpose: Audio player fragment
 */

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.R;
import com.akshathjain.bookworm.async.ArchiveRetriever;
import com.akshathjain.bookworm.interfaces.MusicPlayer;
import com.akshathjain.bookworm.interfaces.QueryFinished;
import com.akshathjain.bookworm.generic.Chapter;
import com.akshathjain.bookworm.utils.TimeConverter;
import com.bumptech.glide.Glide;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Player extends Fragment implements MusicPlayer {
    private AudioBook book;
    private ImageView playPause;
    private ImageView trackNext;
    private ImageView trackPrevious;
    private SeekBar seekBar;
    private TextView title;
    private TextView subtitle;
    private ImageView thumbnail;
    private TextView currentTime;
    private TextView totalTime;
    private boolean isMusicPlaying = false;
    private boolean lockControls = false;
    private MediaPlayer mediaPlayer;
    private Chapter currentChapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_audio_player, container, false);

        playPause = layout.findViewById(R.id.track_play_pause);
        trackNext = layout.findViewById(R.id.track_next);
        trackPrevious = layout.findViewById(R.id.track_previous);
        seekBar = layout.findViewById(R.id.track_seekbar);
        title = layout.findViewById(R.id.track_title);
        subtitle = layout.findViewById(R.id.track_subtitle);
        thumbnail = layout.findViewById(R.id.track_thumbnail);
        currentTime = layout.findViewById(R.id.track_current_time);
        totalTime = layout.findViewById(R.id.track_total_time);
        mediaPlayer = new MediaPlayer();

        //get the book information and then get the chapter information
        Bundle args = getArguments();
        this.book = (AudioBook) args.getSerializable("AUDIO_BOOK");
        ArchiveRetriever streamGetter = new ArchiveRetriever(book);
        streamGetter.addOnCompleted(new QueryFinished<Void>() {
            @Override
            public void onQueryFinished(Void aVoid) {
                setChapter(book.getCurrentChapter());
                setupSeekBar();
            }
        });
        streamGetter.execute(book.getChaptersURL());

        //set book information
        title.setText(book.getTitle()); //bind title
        Glide.with(this).load(book.getThumbnailURL()).transition(withCrossFade()).into(thumbnail); //bind thumbnail

        //setup onclick listeners
        setupOnClickListeners();

        return layout;
    }

    //function to set up on click listeners
    private void setupOnClickListeners() {
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!lockControls)
                    playPauseMusic();
            }
        });

        trackNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextTrack();
            }
        });

        trackPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousTrack();
            }
        });
    }

    //function to setup seek bar functionality (auto update and seeking)
    private void setupSeekBar() {
        final Handler handler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isMusicPlaying) {
                    seekBar.setProgress((int) (mediaPlayer.getCurrentPosition() / 1000.0));
                }
                handler.postDelayed(this, 500);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                currentTime.setText(TimeConverter.format(i));
                if (fromUser) {
                    if (mediaPlayer != null) {
                        playPauseMusic();
                        mediaPlayer.seekTo(i * 1000);
                        playPauseMusic();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //function to setup mediaplayer / wakelock functionality
    private void setupMediaPlayer(String url) {
        try {
            lockControls = true;
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playPauseMusic();
                    lockControls = false;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            lockControls = false;
        }
    }

    private void setChapter(Chapter newChapter) {
        this.currentChapter = newChapter;
        totalTime.setText(TimeConverter.format((int) currentChapter.getRuntime()));
        subtitle.setText(currentChapter.getTitle());
        seekBar.setProgress(0);
        seekBar.setMax((int) currentChapter.getRuntime());
        setupMediaPlayer(currentChapter.getUrl());
    }

    //function to start/stop playing music
    @Override
    public void playPauseMusic() {
        isMusicPlaying = !isMusicPlaying;
        if (isMusicPlaying) {
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);
            mediaPlayer.start();
        } else {
            playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mediaPlayer.pause();
        }
    }

    @Override
    public void nextTrack() {
        if (book.hasNextChapter()) {
            setChapter(book.getNextChapter());
            if (isMusicPlaying)
                playPauseMusic();
            setupMediaPlayer(currentChapter.getUrl());
        }
    }

    @Override
    public void previousTrack() {
        if (book.hasPreviousChapter()) {
            setChapter(book.getPreviousChapter());
            if (isMusicPlaying)
                playPauseMusic();
            setupMediaPlayer(currentChapter.getUrl());
        }
    }

    @Override
    public void selectTrack(int track) {
        if (book.hasChapter(track)) {
            setChapter(book.getChapter(track));
            if (isMusicPlaying)
                playPauseMusic();
            setupMediaPlayer(currentChapter.getUrl());
        }
    }
}

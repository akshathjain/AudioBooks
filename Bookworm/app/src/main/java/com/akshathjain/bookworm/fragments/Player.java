package com.akshathjain.bookworm.fragments;

/*
Name: Akshath Jain
Date: 10/17/17
Purpose: Audio player fragment
 */

import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.akshathjain.bookworm.dialogs.ChapterPickerDialog;
import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.R;
import com.akshathjain.bookworm.async.ArchiveRetriever;
import com.akshathjain.bookworm.interfaces.MusicPlayer;
import com.akshathjain.bookworm.interfaces.QueryFinished;
import com.akshathjain.bookworm.generic.Chapter;
import com.akshathjain.bookworm.utils.Colors;
import com.akshathjain.bookworm.utils.TimeConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.Serializable;
import java.lang.reflect.Array;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Player extends Fragment implements MusicPlayer, Serializable {
    private AudioBook book;
    private ImageView playPause;
    private ImageView trackNext;
    private ImageView trackPrevious;
    private SeekBar seekBar;
    private TextView title;
    private TextView subtitle;
    private TextView author;
    private ImageView thumbnail;
    private TextView currentTime;
    private TextView totalTime;
    private boolean isMusicPlaying = false;
    private boolean lockControls = false;
    private MediaPlayer mediaPlayer;
    private Chapter currentChapter;
    private FloatingActionButton trackSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View layout = inflater.inflate(R.layout.fragment_audio_player, container, false);

        playPause = layout.findViewById(R.id.track_play_pause);
        trackNext = layout.findViewById(R.id.track_next);
        trackPrevious = layout.findViewById(R.id.track_previous);
        seekBar = layout.findViewById(R.id.track_seekbar);
        title = layout.findViewById(R.id.track_title);
        subtitle = layout.findViewById(R.id.track_subtitle);
        author = layout.findViewById(R.id.track_author);
        thumbnail = layout.findViewById(R.id.track_thumbnail);
        currentTime = layout.findViewById(R.id.track_current_time);
        totalTime = layout.findViewById(R.id.track_total_time);
        trackSelector = layout.findViewById(R.id.track_fab);
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
        author.setText(book.getAuthor()); //bind author
        //Glide.with(this).load(book.getThumbnailURL()).transition(withCrossFade()).into(thumbnail); //bind thumbnail
        Glide.with(this)
                .asBitmap()
                .load(book.getThumbnailURL())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {
                        Colors.createPaletteAsync(resource, new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                System.out.println("generated palette");
                                thumbnail.setBackgroundColor(palette.getMutedColor(0));
                                thumbnail.setImageBitmap(resource);
                            }
                        });
                    }
                });

        //setup onclick listeners
        setupOnClickListeners();

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

        trackSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChapterPickerDialog dialog = new ChapterPickerDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("callback", Player.this);
                bundle.putStringArray("chapterNames", book.getChapterNames());
                dialog.setArguments(bundle);
                dialog.show(getActivity().getFragmentManager(), "chapterPicker");
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
                    if (!lockControls) {
                        if (mediaPlayer != null) {
                            playPauseMusic();
                            mediaPlayer.seekTo(i * 1000);
                            playPauseMusic();
                        }
                    }else
                        seekBar.setProgress(0);
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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    nextTrack();
                }
            });
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

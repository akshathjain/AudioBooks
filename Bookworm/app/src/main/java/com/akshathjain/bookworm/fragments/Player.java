package com.akshathjain.bookworm.fragments;

/*
Name: Akshath Jain
Date: 10/17/17
Purpose: Audio player fragment
 */

import android.app.Fragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.akshathjain.bookworm.activities.MainScreen;
import com.akshathjain.bookworm.dialogs.ChapterPickerDialog;
import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.R;
import com.akshathjain.bookworm.async.ArchiveRetriever;
import com.akshathjain.bookworm.interfaces.MusicPlayer;
import com.akshathjain.bookworm.interfaces.LayoutFinished;
import com.akshathjain.bookworm.interfaces.QueryFinished;
import com.akshathjain.bookworm.generic.Chapter;
import com.akshathjain.bookworm.services.MusicPlayerService;
import com.akshathjain.bookworm.utils.Colors;
import com.akshathjain.bookworm.utils.TimeConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.Serializable;

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

    private Bitmap thumbnailBitmap;
    private ImageView thumbnail;

    private TextView currentTime;
    private TextView totalTime;
    private boolean isMusicPlaying = false;
    private boolean lockControls = false;
    private Chapter currentChapter;
    private FloatingActionButton trackSelector;

    private Intent playIntent;

    //binder variables
    private MusicPlayerService service;
    private boolean serviceBound = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        layoutFinishedCallback.onLayoutFinished(thumbnail);

        //get the book information and then get the chapter information
        Bundle args = getArguments();
        this.book = (AudioBook) args.getSerializable("AUDIO_BOOK");
        ArchiveRetriever streamGetter = new ArchiveRetriever(book);
        streamGetter.addOnCompleted(new QueryFinished<Void>() {
            @Override
            public void onQueryFinished(Void aVoid) {
                musicLoadedCallback.onMusicLoaded();
                playIntent = new Intent(getActivity(), MusicPlayerService.class);
                getActivity().bindService(playIntent, connection, Context.BIND_AUTO_CREATE);
            }
        });
        streamGetter.execute(book.getChaptersURL());

        //set book information
        title.setText(book.getTitle()); //bind title
        author.setText(book.getAuthor()); //bind author
        Glide.with(this)
                .asBitmap()
                .load(book.getThumbnailURL())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {
                        thumbnailBitmap = resource;
                        Colors.createPaletteAsync(resource, new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
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
                if (serviceBound && isMusicPlaying) {
                    seekBar.setProgress((int) (service.getCurrentPosition() / 1000.0));
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
                        if (serviceBound) {
                            playPauseMusic();
                            service.seekTo(i * 1000);
                            playPauseMusic();
                        }
                    } else
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

            service.setupMediaPlayer(url, new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playPauseMusic();
                    lockControls = false;

                    //notification
                    Intent notIntent = new Intent(getActivity(), MainScreen.class);
                    notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendInt = PendingIntent.getActivity(getActivity(), 0,
                            notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    //display notification
                    PendingIntent prevTrack = null;
                    PendingIntent playPause = null;
                    PendingIntent nextTrack = null;
                    Notification notification = new NotificationCompat.Builder(getActivity(), "com.akshathjain.bookworm")
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setSmallIcon(R.drawable.ic_list_white_24dp)
                            .addAction(R.drawable.ic_skip_previous_black_24dp, "Previous", prevTrack)
                            .addAction(R.drawable.ic_pause_black_24dp, "Pause", playPause)
                            .addAction(R.drawable.ic_skip_next_black_24dp, "Next", nextTrack)
                            .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                            .setContentTitle(book.getTitle())
                            .setContentText(book.getCurrentChapter().getTitle())
                            .setLargeIcon(thumbnailBitmap)
                            .build();

                    service.startForeground(6969696, notification);
                }
            }, new MediaPlayer.OnCompletionListener() {
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
        } else {
            playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        service.playPauseMusic();
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

    //interface variable to handle stuff when the music player service is connected
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((MusicPlayerService.MusicServiceBinder) iBinder).getService();
            serviceBound = true;

            //once media player established then setup everything
            setChapter(book.getCurrentChapter());
            setupSeekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceBound = false;
        getActivity().unbindService(connection);
    }

    private LayoutFinished layoutFinishedCallback;

    public void addOnLayoutFinished(LayoutFinished callback) {
        layoutFinishedCallback = callback;
    }

    private MusicLoaded musicLoadedCallback;

    public void addOnMusicLoaded(MusicLoaded callback) {
        this.musicLoadedCallback = callback;
    }

    public interface MusicLoaded {
        void onMusicLoaded();
    }

    public interface PlayerControls {
        void openPlayer();
    }
}

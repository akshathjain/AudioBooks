/**
 * Created by Akshath on 11/6/2017.
 */

package com.akshathjain.bookworm.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.akshathjain.bookworm.interfaces.MusicPlayer;


public class MusicPlayerService extends Service implements MusicPlayer {
    private MediaPlayer mediaPlayer;
    private MusicServiceBinder binder = new MusicServiceBinder();
    private boolean isMusicPlaying = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    public void setupMediaPlayer(String url, MediaPlayer.OnPreparedListener onPrep, MediaPlayer.OnCompletionListener onComplete) throws Exception{
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.setOnPreparedListener(onPrep);
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnCompletionListener(onComplete);
    }

    @Override
    public void playPauseMusic() {
        isMusicPlaying = !isMusicPlaying;
        if (isMusicPlaying) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
    }

    @Override
    public void nextTrack() {

    }

    @Override
    public void previousTrack() {

    }

    @Override
    public void selectTrack(int track) {

    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int millis){
        mediaPlayer.seekTo(millis);
    }

    public class MusicServiceBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
}

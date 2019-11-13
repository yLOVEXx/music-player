/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sample.andremion.musicplayer.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.sample.andremion.musicplayer.model.Song;

import java.io.IOException;

public class PlayService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new PlayBinder();
    private static MediaPlayer mPlayer = new MediaPlayer();
    private static ProgressCounter mCounter = new ProgressCounter();
    private static Song mSongInPlayer;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void play(Song song) {
        if(!isPlaying()){
            if(mSongInPlayer != null && mSongInPlayer.getId() == song.getId()){
                mPlayer.start();
                mCounter.restart();
            }
            else{
                mSongInPlayer = song;
                resetPlayer(mSongInPlayer.getPath());
                mPlayer.start();

                resetCounter((int)(mSongInPlayer.getDuration() / 1000));
                mCounter.start();
            }
        }
        else{
            if(mSongInPlayer.getId() != song.getId()){

                mSongInPlayer = song;
                resetPlayer(mSongInPlayer.getPath());
                mPlayer.start();

                resetCounter((int)(mSongInPlayer.getDuration() / 1000));
                mCounter.start();
            }
        }
    }

    private static void resetPlayer(String path){
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resetCounter(int duration){
        mCounter.release();
        mCounter = new ProgressCounter(duration);
    }

    public static boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public static void pause() {
        mPlayer.pause();

        if(mCounter != null){
            mCounter.pause();
        }
    }

    public int getPosition() {
        if (mCounter != null) {
            return mCounter.getPosition();
        }
        return 0;
    }

    public int getDuration() {
        if(mSongInPlayer == null)
            return 0;
        else
            return (int)(mSongInPlayer.getDuration() / 1000);
    }

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PlayBinder extends Binder {

        public PlayService getService() {
            // Return this instance of PlayService so clients can call public methods
            return PlayService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
        mPlayer.release();
        mCounter.release();
    }
}

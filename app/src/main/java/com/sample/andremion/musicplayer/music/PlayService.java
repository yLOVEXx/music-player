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
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class PlayService extends Service {

    private static final int DURATION = 335;

    // Binder given to clients
    private final IBinder mBinder = new PlayBinder();
    private MediaPlayer mPlayer;
    private ProgressCounter mCounter;


    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void play() {
        mPlayer.start();

        if(mCounter == null){
            mCounter = new ProgressCounter();
            mCounter.start();
        }
        else{
            mCounter.doResume();
        }
    }

    public boolean isPlaying() {
        return mCounter != null && mCounter.isPlaying();
    }

    public void pause() {
        mPlayer.pause();

        if(mCounter != null){
            mCounter.doPause();
        }
    }

    public int getPosition() {
        if (mCounter != null) {
            return mCounter.getPosition();
        }
        return 0;
    }

    public int getDuration() {
        return DURATION;
    }

    public void initMediaPlayer(){
        mPlayer = new MediaPlayer();
        AssetFileDescriptor fd = null;
        try {
            fd = getAssets().openFd("Muroki - 2A0X.mp3");
            mPlayer.setDataSource(fd);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mCounter.interrupt();
    }
}

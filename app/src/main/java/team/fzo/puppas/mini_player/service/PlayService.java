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

package team.fzo.puppas.mini_player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.music.ProgressCounter;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;

import java.io.IOException;

public class PlayService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new PlayBinder();
    private static MediaPlayer mPlayer = new MediaPlayer();
    private static ProgressCounter mCounter = new ProgressCounter();

    private static Song mSongInPlayer;          //当前歌曲
    private static Song mPrevSongInPlayer;      //前一首歌
    private static Bitmap mCoverImage;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void play(Song song) {

        if(mSongInPlayer == null || mSongInPlayer.getId() != song.getId()){
            //为播放器设置新歌且记录旧歌
            mPrevSongInPlayer = mSongInPlayer;
            mSongInPlayer = song;

            resetPlayer(mSongInPlayer.getPath());
            mPlayer.start();

            resetCounter((int)(mSongInPlayer.getDuration() / 1000));
            mCounter.start();
        }
        else{
            if(!isPlaying()){
                //当前播放的歌曲处于暂停状态，重新启动播放器
                mPlayer.start();
                mCounter.restart();
            }
        }
    }

    private static void setCoverImage(Context context){
          mCoverImage = MusicContentUtils.getArtwork(context,
                mSongInPlayer.getId(), mSongInPlayer.getAlbumId(), false);
    }

    public static Bitmap getCoverImage(){
        return mCoverImage;
    }

    public static void play(Song song, Context contex) {

        if(mSongInPlayer == null || mSongInPlayer.getId() != song.getId()){
            //为播放器设置新歌且记录旧歌
            mPrevSongInPlayer = mSongInPlayer;
            mSongInPlayer = song;

            setCoverImage(contex);
            
            resetPlayer(mSongInPlayer.getPath());
            mPlayer.start();

            resetCounter((int)(mSongInPlayer.getDuration() / 1000));
            mCounter.start();
        }
        else{
            if(!isPlaying()){
                //当前播放的歌曲处于暂停状态，重新启动播放器
                mPlayer.start();
                mCounter.restart();
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

    public static void seekTo(int pos){
        //resetPlayer(mSongInPlayer.getPath());
        mPlayer.seekTo(pos * 1000);
        //mPlayer.start();
        mCounter.setPosition(pos);
    }

    public static void pause() {
        mPlayer.pause();

        if(mCounter != null){
            mCounter.pause();
        }
    }

    public static void restart(){
        if(mSongInPlayer != null){
            mPlayer.start();
            mCounter.restart();
        }
    }


    public static int getPosition() {
        if (mCounter != null) {
            return mCounter.getPosition();
        }
        return 0;
    }

    public static int getDuration() {
        if(mSongInPlayer == null)
            return 0;
        else
            return (int)(mSongInPlayer.getDuration() / 1000);
    }

    public static Song getSongInPlayer(){
        return mSongInPlayer;
    }

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

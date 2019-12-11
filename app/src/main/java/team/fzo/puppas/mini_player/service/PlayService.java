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
import android.util.Log;

import team.fzo.puppas.mini_player.activities.MusicListActivity;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.music.ProgressCounter;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;
import team.fzo.puppas.mini_player.utils.PlayerNotificationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
The PlayService is responsible to play the music and
synchronize the time counter
 */
public class PlayService extends Service {
    public static final int LIST_REPEAT = 0;       //列表循环
    public static final int LIST_SHUFFLE = 1;      //随机播放
    private static final int NO_POSITION = -1;

    // Binder given to clients
    private final IBinder mBinder = new PlayBinder();
    private static MediaPlayer sPlayer = new MediaPlayer();
    private static ProgressCounter sCounter = new ProgressCounter();

    //当前歌曲在adapter中的position
    private static int sSongPos = NO_POSITION;
    //下一首歌曲的position
    private static int sNextSongPos = NO_POSITION;
    //record the history information for go backing
    private static List<Integer> sHistoryRecord = new ArrayList<>();

    private static int sPlayMode = LIST_REPEAT;                //播放模式
    private static int sSongListId = -1;             //当前歌曲所在歌单id
    private static Bitmap sCoverImage;          //当前歌曲的专辑图片

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private static void setCoverImage(Context context){
        Song songInPlayer = MusicContentUtils.gSongList.get(sSongPos);

        sCoverImage = MusicContentUtils.getArtwork(context,
                songInPlayer.getSongId(), songInPlayer.getAlbumId(), false);
    }

    public static Bitmap getCoverImage(){
        return sCoverImage;
    }

    public static void play(int pos) {
        if(sSongPos != pos){
            //save to the recent playlist
            if(sSongListId != 1)
                MusicContentUtils.storeInPlaylist(getSongInPlayer(), 1);

            //record the play sequence
            if(sSongPos != NO_POSITION)
                sHistoryRecord.add(sSongPos);
            //为播放器设置新歌且记录旧歌
            sSongPos = pos;

            //calculate the next song will be played
            if(sPlayMode == LIST_REPEAT){
                sNextSongPos = sSongPos == MusicContentUtils.gSongList.size() - 1 ? 0 : sSongPos + 1;
            }
            else if(sPlayMode == LIST_SHUFFLE){
                sNextSongPos = new Random().nextInt(MusicContentUtils.gSongList.size());
            }

            Song songInPlayer = MusicContentUtils.gSongList.get(sSongPos);
            resetPlayer(songInPlayer.getPath());
            sPlayer.start();

            resetCounter((int)(songInPlayer.getDuration() / 1000));
            sCounter.start();

            PlayerNotificationUtils.setRemoteViews(songInPlayer, true);
        }
        else{
            if(!isPlaying()){
                //当前播放的歌曲处于暂停状态，重新启动播放器
                sPlayer.start();
                sCounter.restart();
                PlayerNotificationUtils.setRemoteViews(true);
            }

            if(sPlayMode == LIST_SHUFFLE){
                sNextSongPos = new Random().nextInt(MusicContentUtils.gSongList.size());
            }
        }

        PlayerNotificationUtils.sendPlayerNotification();
    }

    public static void play(Context context, int pos) {

        if(sSongPos != pos){
            //save to the recent playlist
            if(sSongListId != 1)
                MusicContentUtils.storeInPlaylist(getSongInPlayer(), 1);

            //record the play sequence
            if(sSongPos != NO_POSITION)
                sHistoryRecord.add(sSongPos);
            //为播放器设置新歌且记录旧歌
            sSongPos = pos;

            //calculate the next song will be played
            if(sPlayMode == LIST_REPEAT){
                sNextSongPos = sSongPos == MusicContentUtils.gSongList.size() - 1 ? 0 : sSongPos + 1;
            }
            else if(sPlayMode == LIST_SHUFFLE){
                sNextSongPos = new Random().nextInt(MusicContentUtils.gSongList.size());
            }

            setCoverImage(context);

            Song songInPlayer = MusicContentUtils.gSongList.get(sSongPos);
            resetPlayer(songInPlayer.getPath());
            sPlayer.start();

            resetCounter((int)(songInPlayer.getDuration() / 1000));
            sCounter.start();

            PlayerNotificationUtils.setRemoteViews(songInPlayer, true);
        }
        else{
            if(!isPlaying()){
                //当前播放的歌曲处于暂停状态，重新启动播放器
                sPlayer.start();
                sCounter.restart();

                PlayerNotificationUtils.setRemoteViews(true);
            }

            if(sPlayMode == LIST_SHUFFLE){
                sNextSongPos = new Random().nextInt(MusicContentUtils.gSongList.size());
            }
        }

        PlayerNotificationUtils.sendPlayerNotification();
    }

    //当播放不同歌单的歌曲时调用此版本
    public static void play(Context context, int pos, int newSongListId){
        //save to the recent playlist
        if(sSongListId != 1)
            MusicContentUtils.storeInPlaylist(getSongInPlayer(), 1);

        sSongListId = newSongListId;
        MusicContentUtils.getContentFromDb();

        //clear history record
        sHistoryRecord.clear();

        sSongPos = pos;
        //calculate the next song will be played
        if(sPlayMode == LIST_REPEAT){
            sNextSongPos = sSongPos == MusicContentUtils.gSongList.size() - 1 ? 0 : sSongPos + 1;
        }
        else if(sPlayMode == LIST_SHUFFLE){
            sNextSongPos = new Random().nextInt(MusicContentUtils.gSongList.size());
        }

        setCoverImage(context);

        Song songInPlayer = MusicContentUtils.gSongList.get(sSongPos);
        resetPlayer(songInPlayer.getPath());
        sPlayer.start();

        resetCounter((int)(songInPlayer.getDuration() / 1000));
        sCounter.start();

        PlayerNotificationUtils.setRemoteViews(songInPlayer, true);
        PlayerNotificationUtils.sendPlayerNotification();
    }

    public static void play(Context context, int pos, boolean isBack){
        if(sSongPos != pos){
            //save to the recent playlist
            if(sSongListId != 1)
                MusicContentUtils.storeInPlaylist(getSongInPlayer(), 1);

            //record the play sequence
            if(sSongPos != NO_POSITION && !isBack)
                sHistoryRecord.add(sSongPos);
            //为播放器设置新歌且记录旧歌
            sSongPos = pos;

            //calculate the next song will be played
            if(sPlayMode == LIST_REPEAT){
                sNextSongPos = sSongPos == MusicContentUtils.gSongList.size() - 1 ? 0 : sSongPos + 1;
            }
            else if(sPlayMode == LIST_SHUFFLE){
                sNextSongPos = new Random().nextInt(MusicContentUtils.gSongList.size());
            }

            setCoverImage(context);

            Song songInPlayer = MusicContentUtils.gSongList.get(sSongPos);
            resetPlayer(songInPlayer.getPath());
            sPlayer.start();

            resetCounter((int)(songInPlayer.getDuration() / 1000));
            sCounter.start();

            PlayerNotificationUtils.setRemoteViews(songInPlayer, true);
        }
        else{
            if(!isPlaying()){
                //当前播放的歌曲处于暂停状态，重新启动播放器
                sPlayer.start();
                sCounter.restart();

                PlayerNotificationUtils.setRemoteViews(true);
            }

            if(sPlayMode == LIST_SHUFFLE){
                sNextSongPos = new Random().nextInt(MusicContentUtils.gSongList.size());
            }
        }

        PlayerNotificationUtils.sendPlayerNotification();
    }

    private static void resetPlayer(String path){
        try {
            sPlayer.reset();
            sPlayer.setDataSource(path);
            sPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resetCounter(int duration){
        sCounter.release();
        sCounter = new ProgressCounter(duration);
    }

    public static boolean isPlaying() {
        return sPlayer.isPlaying();
    }

    public static void seekTo(int pos){
        sPlayer.seekTo(pos * 1000);
        sCounter.setPosition(pos);
    }

    public static void pause() {
        sPlayer.pause();
        if(sCounter != null){
            sCounter.pause();
        }

        PlayerNotificationUtils.setRemoteViews(false);
        PlayerNotificationUtils.sendPlayerNotification();
    }

    public static void restart(){
        if(sSongPos != NO_POSITION){
            sPlayer.start();
            sCounter.restart();
            PlayerNotificationUtils.setRemoteViews(true);
            PlayerNotificationUtils.sendPlayerNotification();
        }
    }

    public static int getSongPos(){
        return sSongPos;
    }

    public static void setPlayMode(int mode){
        sPlayMode = mode;
    }

    public static int getPlayMode(){
        return sPlayMode;
    }

    public static int getNextSongPos(){
        return sNextSongPos;
    }


    public static int getPosition() {
        if (sCounter != null) {
            return sCounter.getPosition();
        }
        return 0;
    }

    public static int getDuration() {
        if(sSongPos == NO_POSITION)
            return 0;
        else
            return (int)(MusicContentUtils.gSongList.get(sSongPos).getDuration() / 1000);
    }

    public static Song getSongInPlayer(){
        if(sSongPos != NO_POSITION)
            return MusicContentUtils.gSongList.get(sSongPos);
        else
            return null;
    }

    public static int getSongListId(){
        return sSongListId;
    }

    public static void setSongListId(int id){
        sSongListId = id;
    }

    //可能产生删除记录最后一项的副作用
    public static int getPrevSongPos(){
        int pos = -1;
        if(sHistoryRecord.isEmpty()){
            //当历史记录遍历完时，根据播放模式计算新的位置
            if(sPlayMode == LIST_REPEAT){
                pos = sSongPos == 0 ?  MusicContentUtils.gSongList.size() - 1: sSongPos - 1;
            }
            else if(sPlayMode == LIST_SHUFFLE){
                pos = new Random().nextInt(MusicContentUtils.gSongList.size());
            }
        }
        else {
            pos = sHistoryRecord.get(sHistoryRecord.size() - 1);
            sHistoryRecord.remove(sHistoryRecord.size() - 1);
        }

        return pos;
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
        sPlayer.stop();
        sPlayer.release();
        sCounter.release();
    }
}

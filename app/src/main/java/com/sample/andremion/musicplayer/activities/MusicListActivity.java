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

package com.sample.andremion.musicplayer.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.music.MusicCoverView;
import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.model.Song;
import com.sample.andremion.musicplayer.utils.MusicContentUtils;
import com.sample.andremion.musicplayer.adapter.SongAdapter;
import com.sample.andremion.musicplayer.service.PlayService;

public class MusicListActivity extends PlayActivity {

    private View mCoverView;
    private View mTitleView;
    private View mTimeView;
    private View mDurationView;
    private View mProgressView;
    private View mPlayButtonView;

    private IntentFilter intentFilter;
    private SongSelectedReceiver receiver;
    private LocalBroadcastManager broadcastManager;

    /*
    songIndex用来保存当前Activity加载的歌曲，通过
    检查songIndex的值来避免不必要的图像加载
     */
    private int mSongIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mPlayButtonView = findViewById(R.id.play_button);

        //设置播放按钮图片
        if(isPlaying()){
            ((FloatingActionButton)mPlayButtonView).setImageResource(R.drawable.ic_pause_animatable);
        }
        else{
            ((FloatingActionButton)mPlayButtonView).setImageResource(R.drawable.ic_play_animatable);
        }
        initCover();        //加载歌曲图片与信息

        //当接受到 SONG_SELECTED 时设置专辑图片与信息与按钮动画
        broadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("musicPlayer.broadcast.SONG_SELECTED");
        receiver = new SongSelectedReceiver();
        broadcastManager.registerReceiver(receiver, intentFilter);

        mSongIndex = -1;

        //获取读取sd卡的权限
        getPermissionAndContent();

        // Set the recycler adapter
        RecyclerView recyclerView = findViewById(R.id.tracks);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SongAdapter(this, MusicContentUtils.SONG_LIST));
    }

    private void initCover(){
        if(getSongInPlayer() != null){
            /*
            load album with bitmap
           */
            MusicCoverView coverView = (MusicCoverView)mCoverView;
            Song song = getSongInPlayer();
            Bitmap cover = PlayService.getCoverImage();
            coverView.setImageBitmap(cover);
            /*
            update the information in the title
             */
            TextView songName = findViewById(R.id.song_name);
            TextView artistName = findViewById(R.id.artist_name);
            TextView separator = findViewById(R.id.separator);
            songName.setText(song.getName());
            artistName.setText(song.getArtist());
            separator.setText(" - ");
        }
    }


    public void onPlayButtonClick(View view) {
        if(getSongInPlayer() == null)
            return;

        FloatingActionButton playButton = (FloatingActionButton)view;
        AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable)playButton.getDrawable();

        //如果当前音乐正在播放
        if(isPlaying()){
            playDrawable.registerAnimationCallback(new PlayButtonAnimation(true));
            pause();
            playDrawable.start();
        }
        else{
            playDrawable.registerAnimationCallback(new PlayButtonAnimation(false));
            restart();
            playDrawable.start();
        }
    }



    private void getPermissionAndContent(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else{
            if(MusicContentUtils.SONG_LIST.isEmpty()) {
                MusicContentUtils.getContent(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(MusicContentUtils.SONG_LIST.isEmpty())
                        MusicContentUtils.getContent(this);
                }
                else{
                    Toast.makeText(this, "您拒绝了请求", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }

    public void onPaneClick(View view) {
        if(PlayService.getSongInPlayer() != null){

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    new Pair<>(mCoverView, ViewCompat.getTransitionName(mCoverView)),
                    new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)),
                    new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)),
                    new Pair<>(mDurationView, ViewCompat.getTransitionName(mDurationView)),
                    new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)),
                    new Pair<>(mPlayButtonView, ViewCompat.getTransitionName(mPlayButtonView)));

            ActivityCompat.startActivity(this, new Intent(this, DetailActivity.class),
                    options.toBundle());
        }
    }

    /*
    监听Animation的结束，结束时设置新的vector animation
     */
    private class PlayButtonAnimation extends Animatable2.AnimationCallback {

        boolean isPlaying;

        public PlayButtonAnimation(boolean bool){
            isPlaying = bool;
        }

        @Override
        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);

            FloatingActionButton playButton = (FloatingActionButton)mPlayButtonView;
            if(isPlaying){
                playButton.setImageResource(R.drawable.ic_play_animatable);
            }
            else{
                playButton.setImageResource(R.drawable.ic_pause_animatable);
            }
        }
    }


    class SongSelectedReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra("songIndex", -1);
            boolean isPlaying = intent.getBooleanExtra("isPlaying", false);

            if(index != mSongIndex){
                /*
                load album with bitmap
                 */
                MusicCoverView coverView = findViewById(R.id.cover);
                Song song = MusicContentUtils.SONG_LIST.get(index);
                Bitmap cover = PlayService.getCoverImage();
                coverView.setImageBitmap(cover);
                /*
                update the information in the title
                 */
                TextView songName = findViewById(R.id.song_name);
                TextView artistName = findViewById(R.id.artist_name);
                TextView separator = findViewById(R.id.separator);
                songName.setText(song.getName());
                artistName.setText(song.getArtist());
                separator.setText(" - ");

                mSongIndex = index;
            }

            //设置播放按钮动画
            if(!isPlaying) {
                FloatingActionButton playButton = (FloatingActionButton)mPlayButtonView;
                AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable) playButton.getDrawable();
                playDrawable.registerAnimationCallback(new PlayButtonAnimation(false));
                playDrawable.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(receiver);
    }

}

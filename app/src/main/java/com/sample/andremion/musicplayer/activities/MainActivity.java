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
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.AsyncTask;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.music.MusicCoverView;
import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.model.Song;
import com.sample.andremion.musicplayer.music.MusicContent;
import com.sample.andremion.musicplayer.adapter.SongAdapter;
import com.sample.andremion.musicplayer.music.PlayService;

public class MainActivity extends PlayActivity {

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
        setContentView(R.layout.content_list);

        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mPlayButtonView = findViewById(R.id.play_button_in_list);

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
        recyclerView.setAdapter(new SongAdapter(this, MusicContent.SONG_LIST));
    }


    public void onFabClick(View view) {
        if(getSongInPlayer() == null)
            return;

        FloatingActionButton playButton = (FloatingActionButton)view;
        AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable)playButton.getDrawable();

        //如果当前音乐正在播放
        if(isPlaying()){
            pause();
            playDrawable.start();
            new PlayButtonAnimationTask().execute(true);
        }
        else{
            restart();
            playDrawable.start();
            new PlayButtonAnimationTask().execute(false);
        }
    }



    public void getPermissionAndContent(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else{
            if(MusicContent.SONG_LIST.isEmpty()) {
                MusicContent.getContent(this);
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
                    if(MusicContent.SONG_LIST.isEmpty())
                        MusicContent.getContent(this);
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
    延迟设置View中的src动画，防止动画被后续代码截断
     */
    class PlayButtonAnimationTask extends AsyncTask<Boolean, Boolean, Void>{

        @Override
        protected Void doInBackground(Boolean... booleans) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            publishProgress(booleans);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);

            FloatingActionButton playButton = findViewById(R.id.play_button_in_list);
            if(values[0]){
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
                Song song = MusicContent.SONG_LIST.get(index);
                Bitmap cover = MusicContent.getArtwork(MainActivity.this,
                        song.getId(), song.getAlbumId(), false);
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

                //设置播放按钮动画
                if(!isPlaying) {
                    FloatingActionButton playButton = findViewById(R.id.play_button_in_list);
                    AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable) playButton.getDrawable();
                    playDrawable.start();
                    new PlayButtonAnimationTask().execute(false);
                }

                mSongIndex = index;
            }
            else{
                if(!isPlaying) {
                    FloatingActionButton playButton = findViewById(R.id.play_button_in_list);
                    AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable) playButton.getDrawable();
                    playDrawable.start();
                    new PlayButtonAnimationTask().execute(false);
                }
            }
        }
    }

    /*
    当用户回退时保证Activity不销毁
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(receiver);
    }

}

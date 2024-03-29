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

package team.fzo.puppas.mini_player.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


import org.litepal.LitePal;

import java.util.List;

import team.fzo.puppas.mini_player.MyApplication;
import team.fzo.puppas.mini_player.model.MusicList;
import team.fzo.puppas.mini_player.model.song_model.SongInRecent;
import team.fzo.puppas.mini_player.view.MarqueeTextView;
import team.fzo.puppas.mini_player.view.MusicCoverView;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;
import team.fzo.puppas.mini_player.adapter.SongAdapter;
import team.fzo.puppas.mini_player.service.PlayService;

public class MusicListActivity extends PlayActivity {

    private View mCoverView;
    private View mTitleView;
    private TextView mMusicListName;
    private TextView mCounter;


    private SongSelectedReceiver mSongSelectedReceiver;
    private SongFinishedReceiver mSongFinishedReceiver;
    private PrevButtonClickedReceiver mPrevButtonClickedReceiver;
    private PlayButtonClickedReceiver mPlayButtonClickedReceiver;
    private LocalBroadcastManager mBroadcastManager;

    /*
    songIndex用来保存当前Activity加载的歌曲，通过
    检查songIndex的值来避免不必要的图像加载
     */
    private int mSongIndex;
    //当前加载歌单页面的id
    private static int sCurrentListId;
    private List<Song> mCurrentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        //读取由MainActivity传递的歌单id
        Intent intent = getIntent();
        sCurrentListId = intent.getIntExtra("musicListId", 0);

        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title);

        //设置播放按钮图片
        if(isPlaying()){
            mPlayButtonView.setImageResource(R.drawable.ic_pause_animatable);
        }
        else{
            mPlayButtonView.setImageResource(R.drawable.ic_play_animatable);
        }
        initCover();        //加载歌曲图片与信息

        initBroadcastManager();
        mSongIndex = -1;

        // Set the recycler adapter
        mCurrentList = LitePal.order("id desc").find(MusicContentUtils.SONG_LIST_CLASS[sCurrentListId]);

        RecyclerView recyclerView = findViewById(R.id.tracks);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SongAdapter(this, mCurrentList));

        //设置歌单信息
        initSongListInfo(mCurrentList.size());
    }


    private void initSongListInfo(int songNum){
        mMusicListName = findViewById(R.id.music_list_name);
        mCounter = findViewById(R.id.counter);

        MusicList list = LitePal.where("musicListId = ?", String.valueOf(sCurrentListId)).
                find(MusicList.class).get(0);
        mMusicListName.setText(list.getMusicListName());

        list.setSongNum(songNum);
        list.save();
        mCounter.setText(songNum + "首");
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
            MarqueeTextView titleInfo = (MarqueeTextView)mTitleView;
            String info = song.getName() + " - " + song.getArtist();
            titleInfo.setText(info);
        }
    }


    private void initBroadcastManager(){

        //当接受到 SONG_SELECTED 时设置专辑图片与信息与按钮动画
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("musicPlayer.broadcast.SONG_SELECTED");
        mSongSelectedReceiver = new SongSelectedReceiver();
        mBroadcastManager.registerReceiver(mSongSelectedReceiver, intentFilter);

        //receive the broadcast from ProgressCounter
        intentFilter = new IntentFilter();
        intentFilter.addAction("musicPlayer.broadcast.SONG_FINISHED");
        mSongFinishedReceiver = new SongFinishedReceiver();
        mBroadcastManager.registerReceiver(mSongFinishedReceiver, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction("musicPlayer.broadcast.PLAY_BUTTON_CLICKED");
        mPlayButtonClickedReceiver = new PlayButtonClickedReceiver();
        mBroadcastManager.registerReceiver(mPlayButtonClickedReceiver, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction("musicPlayer.broadcast.PREV_BUTTON_CLICKED");
        mPrevButtonClickedReceiver = new PrevButtonClickedReceiver();
        mBroadcastManager.registerReceiver(mPrevButtonClickedReceiver, intentFilter);
    }


    /*
    根据播放状态设置animation
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        initCover();

        if(isPlaying()){
            mPlayButtonView.setImageResource(R.drawable.ic_pause_animatable);
        }
        else{
            mPlayButtonView.setImageResource(R.drawable.ic_play_animatable);
        }

        //update the recycler view
        mCurrentList.clear();
        mCurrentList.addAll(LitePal.order("id desc").find(MusicContentUtils.SONG_LIST_CLASS[sCurrentListId]));
        RecyclerView recyclerView = findViewById(R.id.tracks);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SongAdapter(this, mCurrentList));

        //update the song number
        mCounter.setText(mCurrentList.size() + "首");
    }

    public void onPlayButtonClick(View view) {
        if(getSongInPlayer() == null)
            return;

        AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable)mPlayButtonView.getDrawable();

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


    public void onPaneClick(View view) {
        if(PlayService.getSongInPlayer() != null){

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    new Pair<>(mCoverView, ViewCompat.getTransitionName(mCoverView)),
                    new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)),
                    new Pair<>((View)mTimeView, ViewCompat.getTransitionName(mTimeView)),
                    new Pair<>((View)mDurationView, ViewCompat.getTransitionName(mDurationView)),
                    new Pair<>((View)mProgressView, ViewCompat.getTransitionName(mProgressView)),
                    new Pair<>((View)mPlayButtonView, ViewCompat.getTransitionName(mPlayButtonView)));

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("sender_id", 0);
            ActivityCompat.startActivity(this, intent, options.toBundle());
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
                MusicCoverView coverView = (MusicCoverView)(mCoverView);
                Song song = MusicContentUtils.gSongList.get(index);
                Bitmap cover = PlayService.getCoverImage();
                coverView.setImageBitmap(cover);
                /*
                update the information in the title
                 */
                MarqueeTextView titleInfo = (MarqueeTextView)mTitleView;
                String info = song.getName() + " - " + song.getArtist();
                titleInfo.setText(info);

                mSongIndex = index;
            }

            //设置播放按钮动画
            if(!isPlaying) {
                AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable) mPlayButtonView.getDrawable();
                playDrawable.registerAnimationCallback(new PlayButtonAnimation(false));
                playDrawable.start();
            }
        }
    }

    private class SongFinishedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nextSongPos = intent.getIntExtra("nextSongPos",0);

            MusicCoverView coverView = (MusicCoverView)mCoverView;
            Song song = MusicContentUtils.gSongList.get(nextSongPos);
            Bitmap coverImage = PlayService.getCoverImage();
            coverView.setImageBitmap(coverImage);

            MarqueeTextView titleInfo = (MarqueeTextView)mTitleView;
            String info = song.getName() + " - " + song.getArtist();
            titleInfo.setText(info);

            mPlayButtonView.setImageResource(R.drawable.ic_pause_animatable);

            //update the recycler view
            mCurrentList.clear();
            mCurrentList.addAll(LitePal.order("id desc").find(MusicContentUtils.SONG_LIST_CLASS[sCurrentListId]));
            RecyclerView recyclerView = findViewById(R.id.tracks);
            assert recyclerView != null;
            recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
            recyclerView.setAdapter(new SongAdapter(MyApplication.getContext(), mCurrentList));

            //update the song number
            mCounter.setText(mCurrentList.size() + "首");
        }
    }

    private class PrevButtonClickedReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int prevSongPos = intent.getIntExtra("prevSongPos",0);

            MusicCoverView coverView = (MusicCoverView)mCoverView;
            Song song = MusicContentUtils.gSongList.get(prevSongPos);
            Bitmap coverImage = PlayService.getCoverImage();
            coverView.setImageBitmap(coverImage);

            MarqueeTextView titleInfo = (MarqueeTextView)mTitleView;
            String info = song.getName() + " - " + song.getArtist();
            titleInfo.setText(info);

            mPlayButtonView.setImageResource(R.drawable.ic_pause_animatable);

            //update the recycler view
            mCurrentList.clear();
            mCurrentList.addAll(LitePal.order("id desc").find(MusicContentUtils.SONG_LIST_CLASS[sCurrentListId]));
            RecyclerView recyclerView = findViewById(R.id.tracks);
            assert recyclerView != null;
            recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
            recyclerView.setAdapter(new SongAdapter(MyApplication.getContext(), mCurrentList));

            //update the song number
            mCounter.setText(mCurrentList.size() + "首");
        }
    }

    private class PlayButtonClickedReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果当前音乐正在播放
            if(isPlaying()){
                mPlayButtonView.setImageResource(R.drawable.ic_pause_animatable);
            }
            else{
                mPlayButtonView.setImageResource(R.drawable.ic_play_animatable);
            }
        }
    }


    public static void setCurrentListId(int id){
        sCurrentListId = id;
    }

    public static int getCurrentListId(){
        return sCurrentListId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadcastManager.unregisterReceiver(mSongSelectedReceiver);
        mBroadcastManager.unregisterReceiver(mSongFinishedReceiver);
        mBroadcastManager.unregisterReceiver(mPrevButtonClickedReceiver);
        mBroadcastManager.unregisterReceiver(mPlayButtonClickedReceiver);
    }

}

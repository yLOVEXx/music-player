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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.adapter.TransitionAdapter;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.service.PlayService;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;
import team.fzo.puppas.mini_player.view.MusicCoverView;
import team.fzo.puppas.mini_player.view.MarqueeTextView;

public class DetailActivity extends PlayActivity {
    private static final int UPDATE_SEEKBAR_MESSAGE = 0;

    private Bitmap mCoverImage;      //the image has been resized
    private RangeSeekBar mSeekBar;
    //seekbar是否被拖动的状态
    private boolean mIsSeekBarTracking;

    private IntentFilter mIntentFilter;
    private SongFinishedReceiver mSongFinishedReceiver;
    private LocalBroadcastManager mBroadcastManager;

    private final Handler mUpdateSeekBarHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /*
            call the getPostion() method to set the position of seekbar
            if the seekbar is dragging, seekbar will stop set progress
             */
            final int position = getPosition();
            if(!mIsSeekBarTracking)
                mSeekBar.setProgress(position);
            sendEmptyMessageDelayed(UPDATE_SEEKBAR_MESSAGE, DateUtils.SECOND_IN_MILLIS);
        }
    };

    public static int sender_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCoverImage = PlayService.getCoverImage();
        mCoverImage = imageScale(mCoverImage, 900, 900);

        mCoverView.setImageBitmap(mCoverImage);
        //将trackline的透明度设为1
        mCoverView.setTrackColor(0x01ffffff);

        sender_id = getIntent().getIntExtra("sender_id", -1);
        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                // Nothing to do
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {

            }

            @Override
            public void onBackPressed(MusicCoverView coverView) {
                /*
                调用 finishAfterTransition
                Reverses the Activity Scene entry Transition and triggers the calling Activity to
                reverse its exit Transition.
                 */
                supportFinishAfterTransition();
            }
        });

        if(sender_id == 0) {
            getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    if (isPlaying()) {
                        mCoverView.start();
                    }
                }
            });
        }
        else{
            if(isPlaying())
                mCoverView.start();
        }
        //set the title
        Song song = getSongInPlayer();
        String info = song.getName() + " - " + song.getArtist();
        mTitleView.setText(info);

        //设置播放按钮图片
        if(isPlaying()){
            mPlayButtonView.setImageResource(R.drawable.ic_pause_animatable);
        }
        else{
            mPlayButtonView.setImageResource(R.drawable.ic_play_animatable);
        }

        initSeekBar();

        //设置接收歌曲结束的广播
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("musicPlayer.broadcast.SONG_FINISHED");
        mSongFinishedReceiver = new SongFinishedReceiver();
        mBroadcastManager.registerReceiver(mSongFinishedReceiver, mIntentFilter);
    }

    //设置seekbar的参数与监听事件
    void initSeekBar(){
        mSeekBar = findViewById(R.id.seekbar);
        mSeekBar.setRange(0, getDuration());
        mSeekBar.setProgress(getPosition());
        mIsSeekBarTracking = false;
        mUpdateSeekBarHandler.sendEmptyMessage(UPDATE_SEEKBAR_MESSAGE);

        mSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                view.setIndicatorText(DateUtils.formatElapsedTime((int)leftValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                mIsSeekBarTracking = true;
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                seekTo((int)view.getLeftSeekBar().getProgress());
                view.setProgress(view.getLeftSeekBar().getProgress());
                mIsSeekBarTracking = false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        mCoverView.stop();
    }


    public void onPlayButtonClick(View view) {
        if(getSongInPlayer() == null)
            return;

        AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable)mPlayButtonView.getDrawable();

        //如果当前音乐正在播放
        if(isPlaying()){
            playDrawable.registerAnimationCallback(new PlayButtonAnimation(true));
            pause();
            mCoverView.pause();
            playDrawable.start();
        }
        else{
            playDrawable.registerAnimationCallback(new PlayButtonAnimation(false));
            restart();
            if(mCoverView.isStarted()) {
                mCoverView.resume();
            }
            else{
                mCoverView.start();
            }
            playDrawable.start();
        }
    }

    public void onRepeatClick(View view){
        setPlayMode(PlayService.LIST_REPEAT);
        Toast.makeText(this, "列表循环", Toast.LENGTH_SHORT).show();
    }

    public void onShuffleClick(View view){
        setPlayMode(PlayService.LIST_SHUFFLE);
        Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
    }

    public void onRewindClick(View view){
        if(getPosition() - 3  >= 0)
            seekTo(getPosition() - 3);
        else
            seekTo(0);
        mSeekBar.setProgress(getPosition());
    }

    public void onForwardClick(View view){
        if(getPosition() + 3 <= getDuration())
            seekTo(getPosition() + 3);
        else
            seekTo(getDuration());

        mSeekBar.setProgress(getPosition());
    }

    public void onNextClick(View view){
        AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable)mPlayButtonView.getDrawable();

        if(!isPlaying()){
            playDrawable.registerAnimationCallback(new PlayButtonAnimation(false));
            if(mCoverView.isStarted()) {
                mCoverView.resume();
            }
            else{
                mCoverView.start();
            }
            playDrawable.start();
        }

        int nextSongPos = getNextSongPos();
        play(this, nextSongPos);

        Song song = getSongInPlayer();
        String info = song.getName() + " - " + song.getArtist();
        mTitleView.setText(info);

        mCoverImage = PlayService.getCoverImage();
        mCoverImage = imageScale(mCoverImage, 900, 900);
        mCoverView.setImageBitmap(mCoverImage);

        mSeekBar.setRange(0, getDuration());
    }

    public void onPrevClick(View view){
        AnimatedVectorDrawable playDrawable = (AnimatedVectorDrawable)mPlayButtonView.getDrawable();
        if(!isPlaying()){
            playDrawable.registerAnimationCallback(new PlayButtonAnimation(false));
            if(mCoverView.isStarted()) {
                mCoverView.resume();
            }
            else{
                mCoverView.start();
            }
            playDrawable.start();
        }

        int prevSongPos = getPrevSongPos();
        play(this, prevSongPos);

        Song song = getSongInPlayer();
        String info = song.getName() + " - " + song.getArtist();
        mTitleView.setText(info);

        mCoverImage = PlayService.getCoverImage();
        mCoverImage = imageScale(mCoverImage, 900, 900);
        mCoverView.setImageBitmap(mCoverImage);

        mSeekBar.setRange(0, getDuration());
    }

    private class SongFinishedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nextSongPos = intent.getIntExtra("nextSongPos",0);

            MusicCoverView coverView = mCoverView;
            Song song = MusicContentUtils.gSongList.get(nextSongPos);
            mCoverImage = PlayService.getCoverImage();
            mCoverImage = imageScale(mCoverImage, 900, 900);
            coverView.setImageBitmap(mCoverImage);

            MarqueeTextView titleInfo = mTitleView;
            String info = song.getName() + " - " + song.getArtist();
            titleInfo.setText(info);

            mSeekBar.setRange(0, getDuration());
        }
    }


    private static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadcastManager.unregisterReceiver(mSongFinishedReceiver);
    }
}

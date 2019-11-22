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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.adapter.TransitionAdapter;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.service.PlayService;
import team.fzo.puppas.mini_player.view.MusicCoverView;
import team.fzo.puppas.mini_player.view.MarqueeTextView;

public class DetailActivity extends PlayActivity {

    private MusicCoverView mCoverView;
    private Bitmap mCoverImage;      //the image has been resized
    private MarqueeTextView mTitleView;
    private RangeSeekBar mSeekBar;
    //seekbar是否被拖动的状态
    private boolean mIsSeekBarTracking;


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
            sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCoverImage = PlayService.getCoverImage();
        mCoverImage = imageScale(mCoverImage, 900, 900);

        mCoverView = findViewById(R.id.cover);
        mCoverView.setImageBitmap(mCoverImage);
        //将trackline的透明度设为1
        mCoverView.setTrackColor(0x01ffffff);

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

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                if(isPlaying()) {
                    mCoverView.start();
                }
            }
        });

        //set the title
        mTitleView = findViewById(R.id.title);
        Song song = PlayService.getSongInPlayer();
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
    }

    //设置seekbar的参数与监听事件
    void initSeekBar(){
        mSeekBar = findViewById(R.id.seekbar);
        mSeekBar.setRange(0, getDuration());
        mSeekBar.setProgress(getPosition());
        mIsSeekBarTracking = false;
        mUpdateSeekBarHandler.sendEmptyMessage(0);

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

    public void onRewindClick(View view){
        seekTo(getPosition() - 3);
        mSeekBar.setProgress(getPosition());
    }

    public void onForwardClick(View view){
        seekTo(getPosition() + 3);
        mSeekBar.setProgress(getPosition());
    }


    public void onNextClick(View view){
        int nextSongPos = getNextSongPos();
        play(this, nextSongPos);

        Song song = PlayService.getSongInPlayer();
        String info = song.getName() + " - " + song.getArtist();
        mTitleView.setText(info);

        mCoverImage = PlayService.getCoverImage();
        mCoverImage = imageScale(mCoverImage, 900, 900);
        mCoverView.setImageBitmap(mCoverImage);

        mSeekBar.setRange(0, getDuration());
    }

    public void onPrevClick(View view){
        int prevSongPos = getPrevSongPos();
        play(this, prevSongPos);

        Song song = PlayService.getSongInPlayer();
        String info = song.getName() + " - " + song.getArtist();
        mTitleView.setText(info);

        mCoverImage = PlayService.getCoverImage();
        mCoverImage = imageScale(mCoverImage, 900, 900);
        mCoverView.setImageBitmap(mCoverImage);

        mSeekBar.setRange(0, getDuration());
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
}

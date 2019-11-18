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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.TextView;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.service.PlayService;
import team.fzo.puppas.mini_player.view.ProgressView;


/*
PlayActivity为程序其他Activity的基类
通过bind PlayService负责程序后台音乐的播放与view组件的同步
 */
public abstract class PlayActivity extends AppCompatActivity {

    private PlayService mService;
    private boolean mBound = false;
    protected TextView mTimeView;
    protected TextView mDurationView;
    protected ProgressView mProgressView;
    protected FloatingActionButton mPlayButtonView;


    private final Handler mUpdateProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int position = mService.getPosition();
            final int duration = mService.getDuration();
            onUpdateProgress(position, duration);
            sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
        }
    };
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to PlayService, cast the IBinder and get PlayService instance
            PlayService.PlayBinder binder = (PlayService.PlayBinder) service;
            mService = binder.getService();
            mBound = true;
            onBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mBound = false;
            onUnbind();
        }
    };

    private void onUpdateProgress(int position, int duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatElapsedTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatElapsedTime(duration));
        }
        if (mProgressView != null) {
            mProgressView.setMax(duration);
            mProgressView.setProgress(position);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to PlayService
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mPlayButtonView = findViewById(R.id.play_button);
    }

    @Override
    protected void onDestroy() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
    }

    private void onBind() {
        mUpdateProgressHandler.sendEmptyMessage(0);
    }

    private void onUnbind() {
        mUpdateProgressHandler.removeMessages(0);
    }

    protected void play(Song song) {
        PlayService.play(song);
    }

    protected void play(Song song, Context context){
        PlayService.play(song, context);
    }

    protected void pause() {
        PlayService.pause();
    }

    protected void restart(){
        PlayService.restart();
    }

    protected Song getSongInPlayer(){
        return PlayService.getSongInPlayer();
    }

    protected boolean isPlaying(){
        return PlayService.isPlaying();
    }

    /*
    监听Animation的结束，结束时设置新的vector animation
     */
    protected class PlayButtonAnimation extends Animatable2.AnimationCallback {

        boolean isPlaying;

        public PlayButtonAnimation(boolean bool){
            isPlaying = bool;
        }

        @Override
        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);

            if(isPlaying){
                mPlayButtonView.setImageResource(R.drawable.ic_play_animatable);
            }
            else{
                mPlayButtonView.setImageResource(R.drawable.ic_pause_animatable);
            }
        }
    }
}

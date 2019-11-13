package com.sample.andremion.musicplayer.music;

import android.util.Log;

import static android.content.ContentValues.TAG;

class ProgressCounter extends Thread {
    private int mDuration;            //歌曲秒数
    private boolean mPaused;
    private int mPosition;

    public ProgressCounter(){
        mPaused = false;
        mPosition = 0;
        mDuration = 0;
    }

    public ProgressCounter(int duration){
        this.mDuration = duration;
        mPaused = false;
        mPosition = 0;
    }


    @Override
    public void run() {
        try {
            while (mPosition < mDuration) {
                if (!mPaused) {
                    mPosition++;
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "Player unbounded");
        }
    }

    void restart() {
        mPaused = false;
    }

    void pause() {
        mPaused = true;
    }

    int getPosition() {
        return mPosition;
    }

    //释放线程
    void release(){
        pause();
        if(isAlive()){
            interrupt();
        }
    }
}

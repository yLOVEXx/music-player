package team.fzo.puppas.mini_player.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import team.fzo.puppas.mini_player.MyApplication;
import team.fzo.puppas.mini_player.activities.PlayActivity;
import team.fzo.puppas.mini_player.service.PlayService;

import static android.content.ContentValues.TAG;


/*
对歌曲的播放进度的计时器
 */
public class ProgressCounter extends Thread {
    private int mDuration;            //歌曲秒数
    private boolean mPaused;
    private int mPosition;
    private LocalBroadcastManager broadcastManager;

    public ProgressCounter(){
        mPaused = false;
        mPosition = 0;
        mDuration = 0;
        broadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
    }

    public ProgressCounter(int duration){
        this.mDuration = duration;
        mPaused = false;
        mPosition = 0;
        broadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
    }


    @Override
    public void run() {
        try {
            while (mPosition < mDuration) {
                if (!mPaused) {
                    mPosition++;
                }
                sleep(1000);    //sleep 1s
            }

            if(mPosition == mDuration){
                int nextSongPos = PlayService.getNextSongPos();
                PlayService.play(MyApplication.getContext(), nextSongPos);

                Intent intent = new Intent("musicPlayer.broadcast.SONG_FINISHED");
                intent.putExtra("nextSongPos", nextSongPos);
                broadcastManager.sendBroadcast(intent);
            }

        } catch (InterruptedException e) {
            Log.d(TAG, "Player unbounded");
        }
    }

    public void restart() {
        mPaused = false;
    }

    public void pause() {
        mPaused = true;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position){
        mPosition = position;
    }

    //释放线程
    public void release(){
        pause();
        if(isAlive()){
            interrupt();
        }
    }
}

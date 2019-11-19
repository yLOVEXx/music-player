package team.fzo.puppas.mini_player.music;

import android.util.Log;

import static android.content.ContentValues.TAG;

public class ProgressCounter extends Thread {
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

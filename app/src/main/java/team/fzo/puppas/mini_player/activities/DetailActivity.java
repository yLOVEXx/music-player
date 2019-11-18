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
import android.transition.Transition;
import android.view.View;

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
                * */
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

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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;

import com.andremion.music.MusicCoverView;
import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.adapter.TransitionAdapter;
import com.sample.andremion.musicplayer.service.PlayService;

public class DetailActivity extends PlayActivity {

    private MusicCoverView mCoverView;
    private Bitmap coverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        coverImage = PlayService.getCoverImage();
        coverImage = imageScale(coverImage, 900, 900);

        mCoverView = findViewById(R.id.cover);
        mCoverView.setImageBitmap(coverImage);
        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                // Nothing to do
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                supportFinishAfterTransition();
            }
        });

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                //play();
                mCoverView.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        onPlayButtonClick(null);
    }

    public void onPlayButtonClick(View view) {
        pause();
        mCoverView.stop();
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

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
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.adapter.TransitionAdapter;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.service.PlayService;

public class DetailActivity extends PlayActivity {

    private MusicCoverView mCoverView;
    private Bitmap mCoverImage;      //the image has been resized
    private LinearLayout mTitleView;

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
                supportFinishAfterTransition();
            }
        });

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                mCoverView.start();
            }
        });

        //set the title
        mTitleView = findViewById(R.id.title);
        Song song = PlayService.getSongInPlayer();
        TextView songName = mTitleView.findViewById(R.id.song_name);
        TextView separator = mTitleView.findViewById(R.id.separator);
        TextView artistName = mTitleView.findViewById(R.id.artist_name);
        songName.setText(song.getName());
        separator.setText(" - ");
        artistName.setText(song.getArtist());
    }

    @Override
    public void onBackPressed() {
        mCoverView.stop();
    }

    public void onPlayButtonClick(View view) {
        pause();
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

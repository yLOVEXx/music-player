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

package team.fzo.puppas.mini_player.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.activities.MusicListActivity;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;
import team.fzo.puppas.mini_player.service.PlayService;

import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private final List<Song> mSongList;
    private final Context mContext;
    private final LocalBroadcastManager broadcastManager;

    public SongAdapter(Context context, List<Song> mSongList) {
        this.mContext = context;
        this.mSongList = mSongList;
        broadcastManager = LocalBroadcastManager.getInstance(mContext);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //获取对应位置的song
        holder.song = mSongList.get(position);

        Bitmap album = MusicContentUtils.getArtwork(mContext,
                holder.song.getId(), holder.song.getAlbumId(), true);
        holder.mCoverView.setImageBitmap(album);

        holder.mTitleView.setText(holder.song.getName());
        holder.mArtistView.setText(holder.song.getArtist());
        holder.mDurationView.setText(MusicContentUtils.formatTime(holder.song.getDuration()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //保持播放歌单与访问歌单的一致
                if(MusicListActivity.getCurrentListId() != PlayService.getSongListId()){

                    PlayService.setSongListId(MusicListActivity.getCurrentListId());
                    MusicContentUtils.getContentFromDb();
                }

                /*
                send broadcast to activity for loading the album cover and starting the play button
                animation when click the song item
                 */
                boolean isPlaying = PlayService.isPlaying();
                PlayService.play(mContext, position);

                Intent intent = new Intent("musicPlayer.broadcast.SONG_SELECTED");
                intent.putExtra("songIndex", holder.getAdapterPosition());
                intent.putExtra("isPlaying", isPlaying);
                broadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mCoverView;
        public final TextView mTitleView;
        public final TextView mArtistView;
        public final TextView mDurationView;
        public Song song;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCoverView = view.findViewById(R.id.cover);
            mTitleView = view.findViewById(R.id.title);
            mArtistView = view.findViewById(R.id.artist);
            mDurationView = view.findViewById(R.id.duration);
        }
    }
}
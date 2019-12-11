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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.LitePal;

import team.fzo.puppas.mini_player.MyActivityManager;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.activities.MusicListActivity;
import team.fzo.puppas.mini_player.model.MusicList;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.utils.DensityUtils;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;
import team.fzo.puppas.mini_player.service.PlayService;

import java.util.List;
import java.util.Objects;


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
                holder.song.getSongId(), holder.song.getAlbumId(), true);
        holder.mCoverView.setImageBitmap(album);

        holder.mTitleView.setText(holder.song.getName());
        holder.mArtistView.setText(holder.song.getArtist());
        holder.mDurationView.setText(MusicContentUtils.formatTime(holder.song.getDuration()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MusicListActivity.getCurrentListId() == PlayService.getSongListId() &&
                        position == PlayService.getSongPos() && PlayService.isPlaying()){
                    //如果重复点击则忽略
                    return;
                }

                boolean isPlaying = PlayService.isPlaying();

                //保持播放歌单与访问歌单的一致
                if(MusicListActivity.getCurrentListId() != PlayService.getSongListId()){
                    PlayService.play(mContext, position, MusicListActivity.getCurrentListId());
                }
                else{
                    PlayService.play(mContext, position);
                }

                /*
                send broadcast to activity for loading the album ic_launcher and starting the play button
                animation when click the song item
                 */
                Intent intent = new Intent("musicPlayer.broadcast.SONG_SELECTED");
                intent.putExtra("songIndex", holder.getAdapterPosition());
                intent.putExtra("isPlaying", isPlaying);
                broadcastManager.sendBroadcast(intent);
            }
        });


        holder.mOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity currentActivity = MyActivityManager.getInstance().getCurrentActivity();
                View  dialogView = currentActivity.getLayoutInflater().inflate(
                        R.layout.dialog_song_detail_info,null);

                ImageView coverView = dialogView.findViewById(R.id.cover);
                TextView songNameView = dialogView.findViewById(R.id.song_name);
                TextView singerNameView = dialogView.findViewById(R.id.singer_name);
                Bitmap coverImage = MusicContentUtils.getArtwork(mContext,
                        holder.song.getSongId(), holder.song.getAlbumId(), true);

                coverView.setImageBitmap(coverImage);
                songNameView.setText(holder.song.getName());
                singerNameView.setText(holder.song.getArtist());

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                AlertDialog dialog = builder.setView(dialogView).create();
                dialog.show();

                setOnAddToPlaylistClickListener(dialog, holder.song);
            }
        });
    }

    //设置点击添加到歌单项的事件
    private void setOnAddToPlaylistClickListener(final AlertDialog songDetailDialog, final Song song){
        Objects.requireNonNull(songDetailDialog.getWindow()).findViewById(R.id.add_to_playlist).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Activity currentActivity = MyActivityManager.getInstance().getCurrentActivity();
                View  playlistDialogView = currentActivity.getLayoutInflater().inflate(
                        R.layout.dialog_add_to_playlist,null);

                List<MusicList> selectedPlaylist = LitePal.where("selectedStatus = ?","1").find(MusicList.class);
                RecyclerView recyclerView = playlistDialogView.findViewById(R.id.playlist);
                assert recyclerView != null;
                PlaylistInAddDialogAdapter playlistInAddDialogAdapter = new PlaylistInAddDialogAdapter(
                        selectedPlaylist, mContext);
                recyclerView.setAdapter(playlistInAddDialogAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                //set the maxHeight of recyclerView
                ViewGroup.LayoutParams lp = recyclerView.getLayoutParams();
                int item_num = mContext.getResources().getInteger(R.integer.max_item_in_add_playlist_dialog);
                int item_height = mContext.getResources().getDimensionPixelSize(R.dimen.playlist_item_height);
                if (selectedPlaylist.size() > item_num) {
                    lp.height = item_height * item_num;
                } else {
                    lp.height = item_height * selectedPlaylist.size();
                }
                recyclerView.setLayoutParams(lp);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                AlertDialog playlistDialog = builder.setView(playlistDialogView).create();
                playlistDialog.show();

                setOnCancelButtonClick(playlistDialog);
                setOnOkButtonClick(playlistDialog, playlistInAddDialogAdapter, song);

                songDetailDialog.dismiss();
            }
        });
    }

    private void setOnCancelButtonClick(final AlertDialog playlistDialog){
        Objects.requireNonNull(playlistDialog.getWindow()).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playlistDialog.dismiss();
            }
        });
    }

    private void setOnOkButtonClick(final AlertDialog playlistDialog,
                                    final PlaylistInAddDialogAdapter playlistInAddDialogAdapter,
                                    final Song song){
        Objects.requireNonNull(playlistDialog.getWindow()).findViewById(R.id.ok).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                List<Integer> playlistId = playlistInAddDialogAdapter.getSelectedPlaylistId();
                for(int i = 0; i < playlistId.size(); ++i){
                    MusicContentUtils.storeInPlaylist(song, playlistId.get(i));
                }

                playlistDialog.dismiss();
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
        public final ImageView mOptionView;
        public Song song;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCoverView = view.findViewById(R.id.cover);
            mTitleView = view.findViewById(R.id.title);
            mArtistView = view.findViewById(R.id.artist);
            mDurationView = view.findViewById(R.id.duration);
            mOptionView = view.findViewById(R.id.song_detail_option);
        }
    }
}

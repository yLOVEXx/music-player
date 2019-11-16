package com.sample.andremion.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.model.MusicListAlbum;
import com.sample.andremion.musicplayer.activities.MusicListActivity;

import java.util.List;

public class MusicListAlbumAdapter extends RecyclerView.Adapter<MusicListAlbumAdapter.ViewHolder> {
    private final List<MusicListAlbum> myMusicList;
    private Context mContext;
   // private final LocalBroadcastManager broadcastManager;
    public MusicListAlbumAdapter(List<MusicListAlbum> myMusicList, Context context){
        this.myMusicList = myMusicList;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final ImageView musicImage;
        public final TextView musicName;
        public final TextView musicNumber;


        public ViewHolder(View view){
            super(view);
            mView = view;
            musicImage=(ImageView)view.findViewById(R.id.music_image);
            musicName=(TextView) view.findViewById(R.id.music_name);
            musicNumber = (TextView)view.findViewById(R.id.music_number);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainlist_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position =holder.getAdapterPosition();
                MusicListAlbum music=myMusicList.get(position);
                //Toast.makeText(v.getContext(),"sdadasdddsad", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext,MusicListActivity.class);
                //  ComponentName componentName = new ComponentName("com.sample.andremion.musicplayer.view", "com.sample.andremion.musicplayer.v");
                // intent.setComponent(componentName);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position){
        MusicListAlbum music = myMusicList.get(position);
        holder.musicImage.setImageResource(music.getImageId());
        holder.musicName.setText(music.getName());
        //holder.musicName.setText(music.getNumber());

    }

    @Override
    public int getItemCount(){
        return myMusicList.size();
    }
}

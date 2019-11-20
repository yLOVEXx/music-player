package team.fzo.puppas.mini_player.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;
import team.fzo.puppas.mini_player.activities.MusicListActivity;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private final List<MusicList> myMusicList;
    private Context mContext;
    public int musicListId;

    public MusicListAdapter(List<MusicList> myMusicList, Context context){
        this.myMusicList = myMusicList;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final ImageView musicImage;
        public final TextView musicName;


        public ViewHolder(View view){
            super(view);
            mView = view;
            musicImage = view.findViewById(R.id.music_image);
            musicName = view.findViewById(R.id.music_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_item, parent,false);
        final ViewHolder holder=new ViewHolder(view);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MusicList music = myMusicList.get(position);
                musicListId = music.getMusicListId();
                Intent intent = new Intent(mContext, MusicListActivity.class);
                intent.putExtra("musicListId", musicListId);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position){
        MusicList music = myMusicList.get(position);
        holder.musicImage.setImageResource(music.getMusicListAlbumId());
        holder.musicName.setText(music.getMusicListName());
    }

    @Override
    public int getItemCount(){
        return myMusicList.size();
    }

}

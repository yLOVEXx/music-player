package team.fzo.puppas.mini_player.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.List;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;

public class PlaylistInAddDialogAdapter extends RecyclerView.Adapter<PlaylistInAddDialogAdapter.ViewHolder>{
    private final List<MusicList> myMusicList;
    private Context mContext;

    public PlaylistInAddDialogAdapter(List<MusicList> myMusicList, Context context){
        this.myMusicList = myMusicList;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final View mView;
        private final ImageView mMusicImage;
        private final TextView mMusicName;
        private final CustomCheckBox mCheckBox;


        public ViewHolder(View view){
            super(view);
            mView = view;
            mMusicImage = view.findViewById(R.id.playlist_picture);
            mMusicName = view.findViewById(R.id.playlist_name);
            mCheckBox = view.findViewById(R.id.check_box);
        }
    }

    @NonNull
    @Override
    public PlaylistInAddDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_item_in_add_dialog, parent,false);
        final PlaylistInAddDialogAdapter.ViewHolder holder = new PlaylistInAddDialogAdapter.ViewHolder(view);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final PlaylistInAddDialogAdapter.ViewHolder holder, final int position){
        MusicList musicList = myMusicList.get(position);
        holder.mMusicImage.setImageResource(musicList.getMusicListAlbumId());
        holder.mMusicName.setText(musicList.getMusicListName());
    }

    @Override
    public int getItemCount(){
        return myMusicList.size();
    }
}

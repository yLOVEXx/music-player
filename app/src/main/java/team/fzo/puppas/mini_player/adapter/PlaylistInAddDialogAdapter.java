package team.fzo.puppas.mini_player.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;

public class PlaylistInAddDialogAdapter extends RecyclerView.Adapter<PlaylistInAddDialogAdapter.ViewHolder>{
    private final List<MusicList> mMyMusicList;
    private Context mContext;
    private List<Boolean> mCheckedList;

    public PlaylistInAddDialogAdapter(List<MusicList> myMusicList, Context context){
        this.mMyMusicList = myMusicList;
        mContext = context;
        mCheckedList = new ArrayList<>();
        for(int i = 0; i < mMyMusicList.size(); ++i){
            mCheckedList.add(false);
        }
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
        return holder;
    }

    @Override
    public void onBindViewHolder(final PlaylistInAddDialogAdapter.ViewHolder holder, final int position){
        MusicList musicList = mMyMusicList.get(position);
        holder.mMusicImage.setImageResource(musicList.getMusicListAlbumId());
        holder.mMusicName.setText(musicList.getMusicListName());

        holder.mCheckBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                mCheckedList.set(position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mMyMusicList.size();
    }

    public List<Integer> getSelectedPlaylistId(){
        List<Integer> idList = new ArrayList<>();
        for(int i = 0; i < mCheckedList.size(); ++i){
            if(mCheckedList.get(i)){
                idList.add(mMyMusicList.get(i).getMusicListId());
            }
        }

        return idList;
    }
}

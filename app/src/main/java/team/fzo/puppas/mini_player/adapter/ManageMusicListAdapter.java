package team.fzo.puppas.mini_player.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;
import team.fzo.puppas.mini_player.utils.MusicListUtils;

public class ManageMusicListAdapter extends RecyclerView.Adapter<ManageMusicListAdapter.MyViewHolder>  {

        private Context mContext;
        private List<MusicList> mMusicList; //定义数据源
        public int[] mCheckedArray = new int[MusicListUtils.MUSIC_LIST_CATEGORY_NUM-2];


        //定义构造方法，默认传入上下文和数据源
        public ManageMusicListAdapter(Context context, List<MusicList> mMusicList) {
            mContext = context;
            this.mMusicList = mMusicList;
        }


    //定义自己的ViewHolder，将View的控件引用在成员变量上
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mMusicListImage;
        private final TextView mMusicListName;
        private final CheckBox mCheckBox;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mMusicListImage = view.findViewById(R.id.music_list_image);
            mMusicListName = view.findViewById(R.id.music_list_name);
            mCheckBox = view.findViewById(R.id.checkBox);
        }
    }

        @NonNull
        @Override  //将ItemView渲染进来，创建ViewHolder
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.manage_music_list_item,parent,false);
            final MyViewHolder myViewHolder = new MyViewHolder(view);
            List<MusicList> mNowLists =  LitePal.findAll(MusicList.class);
                for(int i = 0; i< MusicListUtils.MUSIC_LIST_CATEGORY_NUM-2; i++){
                    mCheckedArray[i] = mNowLists.get(i+2).getSelectedStatus();
                }
            return myViewHolder;
        }

    @Override  //将数据源的数据绑定到相应控件上
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            MusicList musicList = mMusicList.get(position);
            holder.mMusicListImage.setImageResource(musicList.getMusicListAlbumId());
            holder.mCheckBox.setText(musicList.getMusicListName());
            if(musicList.getSelectedStatus()==1){
                holder.mCheckBox.setChecked(true);
            }
            else
                holder.mCheckBox.setChecked(false);

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mCheckedArray[position] = 1;
                }
                else
                    mCheckedArray[position] = 0;
            }
        });
        }

        @Override
        public int getItemCount() {
            if (mMusicList != null) {
                return mMusicList.size();
            }
            return 0;
        }

    }

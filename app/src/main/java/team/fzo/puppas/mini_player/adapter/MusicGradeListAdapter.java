package team.fzo.puppas.mini_player.adapter;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.support.annotation.NonNull;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import team.fzo.puppas.mini_player.R;
        import team.fzo.puppas.mini_player.model.Song;
        import team.fzo.puppas.mini_player.utils.MusicContentUtils;

        import java.util.List;

public class MusicGradeListAdapter extends RecyclerView.Adapter<MusicGradeListAdapter.ViewHolder> {
    private final List<Song> mSongList;
    private Context mContext;

    public MusicGradeListAdapter(List<Song> mSongList, Context context){
        this.mSongList = mSongList;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        private final ImageView mMusicImage;
        private final TextView mMusicName;
        private final ProgressBar mProgressBar;
        public Song song;


        public ViewHolder(View view){
            super(view);
            mView = view;
            mMusicImage = view.findViewById(R.id.music_image_grade);
            mMusicName = view.findViewById(R.id.textView_grade);
            mProgressBar = view.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_grade_list_item, parent,false);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position){

        holder.song = mSongList.get(position);
        Bitmap album = MusicContentUtils.getArtwork(mContext,
                holder.song.getSongId(), holder.song.getAlbumId(), true);
        holder.mMusicImage.setImageBitmap(album);
        holder.mMusicName.setText(holder.song.getName());
        if(position == 0)
            holder.mProgressBar.setProgress(80);
        else if(position == 1)
            holder.mProgressBar.setProgress(70);
        else if(position == 2)
            holder.mProgressBar.setProgress(60);
        else if(position == 3)
            holder.mProgressBar.setProgress(50);
        else if(position == 4)
            holder.mProgressBar.setProgress(30);
        else if(position == 5)
            holder.mProgressBar.setProgress(20);
        else
            holder.mProgressBar.setProgress(0);
    }

    @Override
    public int getItemCount(){
        return mSongList.size();
    }

}

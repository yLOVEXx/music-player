package team.fzo.puppas.mini_player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import org.litepal.LitePal;
import java.util.List;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.adapter.ManageMusicListAdapter;
import team.fzo.puppas.mini_player.model.MusicList;

public class ManageMusicListActivity extends PlayActivity {

    private List<MusicList> mMusicLists;
    private ManageMusicListAdapter mManageMusicListAdapter;
    private List<MusicList> mAllMusicLists;
    private FloatingActionButton mFloatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_music_list);
        mFloatingActionButton = findViewById(R.id.floatingActionButton);
        mMusicLists = LitePal.findAll(MusicList.class);

        mAllMusicLists = LitePal.findAll(MusicList.class);
        mAllMusicLists.remove(1);
        mAllMusicLists.remove(0);

        RecyclerView recyclerView = findViewById(R.id.manage_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this ,3);
        assert recyclerView != null;
        mManageMusicListAdapter = new ManageMusicListAdapter(this,mAllMusicLists);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mManageMusicListAdapter);

        //设置点击事件
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedStatus(mManageMusicListAdapter.mCheckedArray);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    //改变选中项目的状态
    public void setSelectedStatus(int[] checkedArray){
        for(int i = 0; i < checkedArray.length; i++){
            mMusicLists.get(i+2).setSelectedStatus(checkedArray[i]);
            mMusicLists.get(i+2).save();
        }
    }

}

package team.fzo.puppas.mini_player.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import org.litepal.LitePal;

import java.util.List;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.adapter.MusicGradeListAdapter;
import team.fzo.puppas.mini_player.model.Song;

public class MusicGradeActivity extends AppCompatActivity {

    private MusicGradeListAdapter mMusicGradeListAdapter;
    private List<Song> mSongs;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_grade);

        mSongs = LitePal.findAll(Song.class);


        mFloatingActionButton = findViewById(R.id.floatingActionButton1);
        RecyclerView recyclerView = findViewById(R.id.music_grade_recycler_view);
        assert recyclerView != null;
        mMusicGradeListAdapter = new MusicGradeListAdapter(mSongs,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mMusicGradeListAdapter);

        //设置点击事件
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            //此处写退向后台的处理
            MainActivity.mNavigationView.setCheckedItem(R.id.nav_1);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

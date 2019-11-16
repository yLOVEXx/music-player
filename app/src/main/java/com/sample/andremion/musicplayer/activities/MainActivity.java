package com.sample.andremion.musicplayer.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.donkingliang.banner.CustomBanner;
import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.model.MusicListAlbum;
import com.sample.andremion.musicplayer.adapter.MusicListAlbumAdapter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends PlayActivity {

    private CustomBanner<Integer> mBanner;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private List<MusicListAlbum> music = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        Button button = findViewById(R.id.button);
        ImageButton imageButton = findViewById(R.id.imageButton);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_white);
        }

        mBanner = findViewById(R.id.banner);

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.aaa);
        setImageToBanner(images);


        mNavigationView.setCheckedItem(R.id.nav_call);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        initMusicList();
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MusicListAlbumAdapter(music,this));

    }

    private void initMusicList(){
        MusicListAlbum myfavorite=new MusicListAlbum("我喜欢的音乐",R.drawable.collect);
        music.add(myfavorite);

        MusicListAlbum classmusic=new MusicListAlbum("古典音乐",R.drawable.collect);
        music.add(classmusic);

        MusicListAlbum popmusic=new MusicListAlbum("流行音乐",R.drawable.collect);
        music.add(popmusic);

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private void setImageToBanner(final ArrayList<Integer> images) {
        mBanner.setPages(new CustomBanner.ViewCreator<Integer>() {
            @Override
            public View createView(Context context, int position) {
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            }

            @Override
            public void updateUI(Context context, View view, int position, Integer entity) {
                Glide.with(context).load(entity).into((ImageView) view);
            }
        }, images).startTurning(5000);
    }

    /*
    当用户回退时保证Activity不销毁
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


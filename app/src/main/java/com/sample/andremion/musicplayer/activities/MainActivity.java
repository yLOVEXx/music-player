package com.sample.andremion.musicplayer.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {

    private CustomBanner<String> mBanner;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mToggle;
    private List<MusicListAlbum> music =new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button button=(Button)findViewById(R.id.button);
        ImageButton imageButton=(ImageButton)findViewById(R.id.imageButton);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        ActionBar actionBar=getSupportActionBar();

        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_white);
        }
/*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ComponentName componentName = new ComponentName("com.example.ltzj", "com.example.ltzj.MainActivity");
                Intent intent = new Intent(
                        com.sample.andremion.musicplayer.activities.MainActivity.this,
                        com.sample.andremion.musicplayer.activities.MusicListActivity.class);
                //intent.setComponent(componentName);
                startActivity(intent);
            }
        });
*/

        mBanner = (CustomBanner) findViewById(R.id.banner);

        ArrayList<String> images = new ArrayList<>();
        images.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3535338527,4000198595&fm=23&gp=0.jpg");
        images.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1017904219,2460650030&fm=23&gp=0.jpg");
        images.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2863927798,667335035&fm=23&gp=0.jpg");
        images.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3885596348,1190704919&fm=23&gp=0.jpg");
        images.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1597254274,1405139366&fm=23&gp=0.jpg");
        images.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3513269361,2662598514&fm=23&gp=0.jpg");
        setBean(images);


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

    private void setBean(final ArrayList<String> beans) {
        mBanner.setPages(new CustomBanner.ViewCreator<String>() {
            @Override
            public View createView(Context context, int position) {
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            }

            @Override
            public void updateUI(Context context, View view, int position, String entity) {
                Glide.with(context).load(entity).into((ImageView) view);
            }
        }, beans).startTurning(5000);
//                //设置指示器为普通指示器
//                .setIndicatorStyle(CustomBanner.IndicatorStyle.ORDINARY)
//                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//                .setIndicatorRes(R.drawable.shape_point_select, R.drawable.shape_point_unselect)
//                //设置指示器的方向
//                .setIndicatorGravity(CustomBanner.IndicatorGravity.CENTER)
//                //设置指示器的指示点间隔
//                .setIndicatorInterval(20)
        //设置自动翻页

    }

//    //设置普通指示器
//    private void setBean(final ArrayList beans) {
//        mBanner.setPages(new CustomBanner.ViewCreator<String>() {
//            @Override
//            public View createView(Context context, int position) {
//                ImageView imageView = new ImageView(context);
//                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                return imageView;
//            }
//
//            @Override
//            public void updateUI(Context context, View view, int position, String entity) {
//                Glide.with(context).load(entity).into((ImageView) view);
//            }
//        }, beans)
//                //设置指示器为普通指示器
//                .setIndicatorStyle(CustomBanner.IndicatorStyle.NUMBER)
//                //设置指示器的方向
//                .setIndicatorGravity(CustomBanner.IndicatorGravity.RIGHT)
//                //设置自动翻页
//                .startTurning(5000);
//    }

//    //设置没有指示器
//    private void setBean(final ArrayList beans) {
//        mBanner.setPages(new CustomBanner.ViewCreator<String>() {
//            @Override
//            public View createView(Context context, int position) {
//                ImageView imageView = new ImageView(context);
//                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                return imageView;
//            }
//
//            @Override
//            public void updateUI(Context context, View view, int position, String entity) {
//                Glide.with(context).load(entity).into((ImageView) view);
//            }
//        }, beans)
//                //设置没有指示器
//                .setIndicatorStyle(CustomBanner.IndicatorStyle.NONE)
//                //设置自动翻页
//                .startTurning(5000);
//    }
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


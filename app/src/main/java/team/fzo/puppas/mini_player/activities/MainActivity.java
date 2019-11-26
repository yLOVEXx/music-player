package team.fzo.puppas.mini_player.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.donkingliang.banner.CustomBanner;

import org.litepal.LitePal;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.adapter.MusicListAdapter;
import team.fzo.puppas.mini_player.broadcast_receiver.NotificationClickReceiver;
import team.fzo.puppas.mini_player.model.MusicList;
import team.fzo.puppas.mini_player.utils.MusicListUtils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends PlayActivity {

    static final int PLAYER_NOTIFICATION_ID = 1;

    private CustomBanner<Integer> mBanner;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private PopupWindow mPopWindow;
    private MusicListAdapter musicListAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    /*
    in order to ensure that the address of mSelectedMusicLists can't
    be changed, we are not able to assign the value to it
     */
    private List<MusicList> mSelectedMusicLists;
    private NotificationManager mNotificationManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //下拉刷新
        mSwipeRefresh = findViewById(R.id.refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMusicList();
            }
        });

        ImageView imageView = findViewById(R.id.addAlbum);
        //设置toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_white);
        }

        //实现左滑菜单的点击效果
        mNavigationView.setCheckedItem(R.id.nav_call);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //初始化轮播图
        mBanner = findViewById(R.id.banner);
        ArrayList<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner_pic1);
        bannerImages.add(R.drawable.banner_pic2);
        bannerImages.add(R.drawable.banner_pic3);
        bannerImages.add(R.drawable.banner_pic4);
        bannerImages.add(R.drawable.banner_pic5);
        setImageToBanner(bannerImages);

        //初始化和加载RecyclerView
       // getPermissionAndContent();   //写入数据库
        if(LitePal.findAll(MusicList.class).isEmpty())
            MusicListUtils.getListContent();

        mSelectedMusicLists = LitePal.where("selectedStatus = ?","1").find(MusicList.class);

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        assert recyclerView != null;
        musicListAdapter = new MusicListAdapter(mSelectedMusicLists,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicListAdapter);

        //点击事件实现弹出菜单
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showPopupWindow();
            }
        });

        sendPlayerNotification();
    }



    //选择菜单栏
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

    //设置轮播图
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

    //实现底部弹出菜单
    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_list, null);
        mPopWindow = new PopupWindow(contentView,
                DrawerLayout.LayoutParams.FILL_PARENT, 425
                , true);
        mPopWindow.setContentView(contentView);
        bgAlpha(0.3f);

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //popupwindow消失时使背景不透明
                bgAlpha(1f);
            }
        });

        //设置各个控件的点击响应
        Button addAlbum = contentView.findViewById(R.id.addalbum);
        Button deleteAlbum = contentView.findViewById(R.id.deletealbum);
        Button shareAlbum = contentView.findViewById(R.id.share);

        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManageMusicListActivity.class);
                mPopWindow.dismiss();
                startActivityForResult(intent,1);
            }
        });

        deleteAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManageMusicListActivity.class);
                startActivityForResult(intent,1);
                mPopWindow.dismiss();
            }
        });

        shareAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"clicked share",Toast.LENGTH_SHORT).show();
                mPopWindow.dismiss();
            }
        });

        //显示PopupWindow
        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mPopWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    //设置弹出菜单背景的透明度
    private void bgAlpha(float bgAlpha) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = bgAlpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);
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

    //初始化歌单列表
    public void initMusicList(){
        mSelectedMusicLists.clear();
        mSelectedMusicLists.addAll(
                LitePal.where("selectedStatus = ?","1").find(MusicList.class));
    }

    //设置返回页面时的调用结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    initMusicList();
                    musicListAdapter.notifyDataSetChanged();
                }
                break;

            default:
        }
    }

    //刷新歌单列表
    private void refreshMusicList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMusicList();
                musicListAdapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
            }
        }).start();
    }


    //发送播放器视图通知
    public void sendPlayerNotification() {
        // 获取系统 通知管理 服务
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews normalViews = new RemoteViews(getPackageName(), R.layout.notification_normal);
        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.notification_big);

        // 构建 Notification
        Notification.Builder builder = new Notification.Builder(this);
        builder.setCustomContentView(normalViews)
                .setCustomBigContentView(bigViews)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true);

        // 设置通知的点击行为：这里启动一个 Activity
        Intent intent = new Intent(this, NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // 兼容  API 26，Android 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel notificationChannel = new NotificationChannel(
                    "playerNotification", "playerNotificationChannel", NotificationManager.IMPORTANCE_DEFAULT);
            // 注册通道，注册后除非卸载再安装否则不改变
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId("playerNotification");
        }
        // 发出通知
        assert mNotificationManager != null;
        mNotificationManager.notify(PLAYER_NOTIFICATION_ID, builder.build());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(PLAYER_NOTIFICATION_ID);
    }
}


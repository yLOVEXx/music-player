package team.fzo.puppas.mini_player.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;

import team.fzo.puppas.mini_player.MyApplication;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.broadcast_receiver.NotificationClickReceiver;
import team.fzo.puppas.mini_player.model.Song;

public class PlayerNotificationUtils {
    private static final int PLAYER_NOTIFICATION_ID = 1;

    private static NotificationManager sNotificationManager;
    private static Notification.Builder sBuilder;

    private static RemoteViews sNormalViews;
    private static RemoteViews sBigViews;

    //初始化RemoteViews与NotificationManager, 并且设置pendingIntent
    public static void initNotification(){
        Context context = MyApplication.getContext();

        sNormalViews = new RemoteViews(context.getPackageName(), R.layout.notification_normal);
        sBigViews = new RemoteViews(context.getPackageName(), R.layout.notification_big);

        //设置next button 点击事件
        Intent intent = new Intent("musicPlayer.broadcast.NOTIFICATION_NEXT_CLICKED");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        sNormalViews.setOnClickPendingIntent(R.id.next, pendingIntent);
        sBigViews.setOnClickPendingIntent(R.id.next, pendingIntent);

        //设置play button 点击事件
        intent = new Intent("musicPlayer.broadcast.NOTIFICATION_PLAY_BUTTON_CLICKED");
        pendingIntent = PendingIntent.getBroadcast(context, 2,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        sNormalViews.setOnClickPendingIntent(R.id.play_button, pendingIntent);
        sBigViews.setOnClickPendingIntent(R.id.play_button, pendingIntent);

        // 获取系统 通知管理 服务
        sNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 构建 Notification
        sBuilder = new Notification.Builder(context);
        sBuilder.setCustomContentView(sNormalViews)
                .setCustomBigContentView(sBigViews)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true);

        // 设置通知的点击行为：这里发送广播
        intent = new Intent(context, NotificationClickReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        sBuilder.setContentIntent(pendingIntent);


        // 兼容  API 26，Android 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel notificationChannel = new NotificationChannel(
                    "playerNotification", "playerNotificationChannel", NotificationManager.IMPORTANCE_DEFAULT);
            // 注册通道，注册后除非卸载再安装否则不改变
            assert sNotificationManager != null;
            sNotificationManager.createNotificationChannel(notificationChannel);
            sBuilder.setChannelId("playerNotification");
        }
    }

    public static void sendPlayerNotification(){
        sBuilder.setCustomContentView(sNormalViews)
                .setCustomBigContentView(sBigViews)
                .setPriority(Notification.PRIORITY_MAX);

        // 发出通知
        assert sNotificationManager != null;
        sNotificationManager.notify(PLAYER_NOTIFICATION_ID, sBuilder.build());
    }


    /*
    song: 当前加载的歌曲
    isPlaying: 当前的播放状态
     */
    public static void setRemoteViews(Song song, boolean isPlaying){
        if(isPlaying){
            sNormalViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_pause);
            sBigViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_pause);
        }
        else{
            sNormalViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_play);
            sBigViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_play);
        }


        sNormalViews.setTextViewText(R.id.song_name, song.getName());
        sNormalViews.setTextViewText(R.id.singer_name, song.getArtist());
        sBigViews.setTextViewText(R.id.song_name, song.getName());
        sBigViews.setTextViewText(R.id.singer_name, song.getArtist());

        Bitmap coverImage = MusicContentUtils.getArtwork(MyApplication.getContext(),
                song.getId(), song.getAlbumId(), true);

        sNormalViews.setImageViewBitmap(R.id.cover, coverImage);
        sBigViews.setImageViewBitmap(R.id.cover, coverImage);
    }

    public static void setRemoteViews(boolean isPlaying){
        if(isPlaying){
            sNormalViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_pause);
            sBigViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_pause);
        }
        else{
            sNormalViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_play);
            sBigViews.setImageViewResource(R.id.play_button, R.drawable.ic_notification_play);
        }
    }

    public static void clearNotification(){
        sNotificationManager.cancel(PLAYER_NOTIFICATION_ID);
    }
}

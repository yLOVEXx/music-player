package team.fzo.puppas.mini_player.broadcast_receiver;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import team.fzo.puppas.mini_player.MyActivityManager;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.activities.DetailActivity;
import team.fzo.puppas.mini_player.activities.MusicListActivity;
import team.fzo.puppas.mini_player.service.PlayService;

public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Activity currentActivity = MyActivityManager.getInstance().getCurrentActivity();
        String currentActivityName = "activities.MusicListActivity";

        if(currentActivity.getLocalClassName().equals(currentActivityName)){

            View coverView = currentActivity.findViewById(R.id.cover);
            View titleView = currentActivity.findViewById(R.id.title);
            View timeView = currentActivity.findViewById(R.id.time);
            View durationView = currentActivity.findViewById(R.id.duration);
            View progressView = currentActivity.findViewById(R.id.progress);
            View playButtonView = currentActivity.findViewById(R.id.play_button);
            ViewCompat.getTransitionName(coverView);

            if(PlayService.getSongInPlayer() != null){
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(currentActivity,
                        new Pair<>(coverView, ViewCompat.getTransitionName(coverView)),
                        new Pair<>(titleView, ViewCompat.getTransitionName(titleView)),
                        new Pair<>(timeView, ViewCompat.getTransitionName(timeView)),
                        new Pair<>(durationView, ViewCompat.getTransitionName(durationView)),
                        new Pair<>(progressView, ViewCompat.getTransitionName(progressView)),
                        new Pair<>(playButtonView, ViewCompat.getTransitionName(playButtonView)));

                Intent myIntent = new Intent(currentActivity, DetailActivity.class);
                myIntent.putExtra("sender_id", 0);
                ActivityCompat.startActivity(currentActivity, myIntent, options.toBundle());
            }
        }
        else{
            Intent myIntent = new Intent(context, DetailActivity.class);
            context.startActivity(myIntent);
        }
    }
}

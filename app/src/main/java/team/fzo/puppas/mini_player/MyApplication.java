package team.fzo.puppas.mini_player;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import org.litepal.LitePal;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override public void onActivityStarted(Activity activity) {

            }

            @Override public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override public void onActivityPaused(Activity activity) {

            }

            @Override public void onActivityStopped(Activity activity) {

            }

            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static Context getContext(){
        return context;
    }
}

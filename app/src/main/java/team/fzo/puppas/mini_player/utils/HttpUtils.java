package team.fzo.puppas.mini_player.utils;

import android.app.VoiceInteractor;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtils {
    public void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                            .url(address)
                            .build();
        client.newCall(request).enqueue(callback);
    }
}

package team.fzo.puppas.mini_player.utils;

import android.app.VoiceInteractor;

import com.alibaba.fastjson.JSON;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtils {
    public static final MediaType JSON_TYPE
            = MediaType.get("application/json; charset=utf-8");

    public static void sendHttpRequestByGet(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                            .url(address)
                            .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendHttpRequestByPost(String address, Object object, Callback callback){
        OkHttpClient client = new OkHttpClient();

        String json = JSON.toJSONString(object);
        RequestBody body = RequestBody.create(JSON_TYPE, json);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void sendHttpRequestByPost(String address, String username,
                                              String password,Callback callback){
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                            .add("name", username)
                            .add("password", password)
                            .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}

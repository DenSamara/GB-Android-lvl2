package com.home.konovaloff.homework;

import android.os.Handler;

import com.home.konovaloff.homework.global.Global;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpRequester {
    public static final String TAG = OkHttpRequester.class.getSimpleName();
    public static final String HTTP_PREFIX = "https://";
    public static final String HTTPS_PREFIX = "https://";

    private OkHttpClient client;
    private OnResponseCompleted listener;

    public OkHttpRequester(OnResponseCompleted listener) {
        client = new OkHttpClient();
        this.listener = listener;
    }

    public void run(String url) {
        if (!url.startsWith(HTTP_PREFIX) || !url.startsWith(HTTPS_PREFIX)) {
            url = HTTPS_PREFIX + url;
        }
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        Request request = builder.build(); // Построим запрос

        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {

            final Handler handler = new Handler();

            @Override
            public void onFailure(Call call, IOException e) {
                Global.log_e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String unswer = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onCompleted(unswer);
                    }
                });
            }
        });
    }

    public interface OnResponseCompleted {
        void onCompleted(String content);
    }
}

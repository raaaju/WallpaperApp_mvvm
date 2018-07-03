package com.georgcantor.wallpaperapp.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.georgcantor.wallpaperapp.MyApplication;
import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Pic;
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor;
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchPorscheTask extends AsyncTask<Void, Void, Pic> {

    private Pic picResult = new Pic();
    private Context context;
    private AsyncResponse output;
    private int index;

    public FetchPorscheTask(Context context, AsyncResponse output, int index) {
        this.context = context;
        this.output = output;
        this.index = index;
    }

    @Override
    protected Pic doInBackground(Void... params) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addNetworkInterceptor(new ResponseCacheInterceptor());
        httpClient.addInterceptor(new OfflineResponseCacheInterceptor());
        httpClient.cache(new Cache(new File(MyApplication.getInstance()
                .getCacheDir(), "ResponsesCache"), 10 * 1024 * 1024));
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);

        ApiService client = ApiClient.getClient(httpClient).create(ApiService.class);
        Call<Pic> call;
        call = client.getPorschePic(index);
        call.enqueue(new Callback<Pic>() {
            @Override
            public void onResponse(Call<Pic> call, Response<Pic> response) {
                try {
                    if (!response.isSuccessful()) {
                        Log.d(context.getResources().getString(R.string.No_Success),
                                response.errorBody().string());
                    } else {
                        picResult = response.body();
                        if (picResult != null) {
                            output.processFinish(picResult);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Pic> call, Throwable t) {
                Toast toast = Toast.makeText(context, context.getResources()
                        .getString(R.string.wrong_message), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        return picResult;
    }
}

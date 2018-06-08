package com.georgcantor.wallpaperapp.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Pic;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchBlackTask extends AsyncTask<Void, Void, Pic> {

    private Pic picResult = new Pic();
    private Context context;
    private AsyncResponse output;
    private int index;

    public FetchBlackTask(Context context, AsyncResponse output, int index) {
        this.context = context;
        this.output = output;
        this.index = index;
    }

    @Override
    protected Pic doInBackground(Void... params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.pixabay_api_link))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService client = retrofit.create(ApiService.class);
        Call<Pic> call;
        call = client.getBlackPic(index);
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
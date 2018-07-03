package com.georgcantor.wallpaperapp.network;

import com.georgcantor.wallpaperapp.BuildConfig;
import com.georgcantor.wallpaperapp.model.Pic;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("?key=" + BuildConfig.API_KEY + "&q=mercedes-benz")
    Call<Pic> getMercedesPic(@Query("page") int index);

    @GET("?key=" + BuildConfig.API_KEY)
    Call<Pic> getCatPic(@Query("category") String category,
                        @Query("page") int index);

    @GET("?key=" + BuildConfig.API_KEY + "&q=bmw")
    Call<Pic> getBmwPic(@Query("page") int index);

    @GET("?key=" + BuildConfig.API_KEY + "&q=porsche")
    Call<Pic> getPorschePic(@Query("page") int index);

    @GET("?key=" + BuildConfig.API_KEY + "&q=audi")
    Call<Pic> getAudiPic(@Query("page") int index);

    @GET("?key=" + BuildConfig.API_KEY + "&q=bugatti")
    Call<Pic> getBugattiPic(@Query("page") int index);

    @GET("?key=" + BuildConfig.API_KEY + "&q=ferrari")
    Call<Pic> getFerrariPic(@Query("page") int index);

    @GET("?key=" + BuildConfig.API_KEY + "&q=lamborghini")
    Call<Pic> getLamboPic(@Query("page") int index);
}

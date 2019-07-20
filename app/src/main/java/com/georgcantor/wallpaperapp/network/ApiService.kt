package com.georgcantor.wallpaperapp.network

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.Pic

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("?key=" + BuildConfig.API_KEY)
    fun getPictures(@Query("q") query: String,
                    @Query("page") index: Int): Call<Pic>
}

package com.georgcantor.wallpaperapp.network

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.Pic

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("?key=" + BuildConfig.API_KEY + "&q=mercedes-benz")
    fun getMercedesPic(@Query("page") index: Int): Call<Pic>

    @GET("?key=" + BuildConfig.API_KEY)
    fun getCatPic(@Query("category") category: String,
                  @Query("page") index: Int): Call<Pic>

    @GET("?key=" + BuildConfig.API_KEY + "&q=bmw")
    fun getBmwPic(@Query("page") index: Int): Call<Pic>

    @GET("?key=" + BuildConfig.API_KEY)
    fun getSearchResults(@Query("q") query: String,
                         @Query("page") index: Int): Call<Pic>
}

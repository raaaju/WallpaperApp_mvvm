package com.georgcantor.wallpaperapp.model.remote

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.model.unsplash.UnsplashResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("?key=" + BuildConfig.API_KEY)
    fun getPictures(
        @Query("q") query: String,
        @Query("page") index: Int
    ): Observable<Pic>

    @GET
    fun getBmw(@Url url: String,
               @Query("page") page: Int): Observable<UnsplashResponse>

    //563492ad6f917000010000012802dcdebfc2451ab06a0cdf7c62fe16 Pexels
    //814e0183c60488e937f966c79d38a31401fcceb515aeee2448292a301665761e Unsplash

}

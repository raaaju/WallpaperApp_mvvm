package com.georgcantor.wallpaperapp.model.remote

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.data.pexels.PhotoResponse
import com.georgcantor.wallpaperapp.model.data.pixabay.Pic
import com.georgcantor.wallpaperapp.model.data.unsplash.UnsplashResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("?key=" + BuildConfig.PIXABAY_KEY)
    fun getPixabayPictures(
        @Query("q") query: String,
        @Query("page") index: Int
    ): Observable<Pic>

    @GET
    fun getUnsplashPictures(
        @Url url: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Observable<UnsplashResponse>

    @GET
    fun getPexelsPictures(
        @Url url: String,
        @Query("query") query: String,
        @Query("per_page") count: Int,
        @Query("page") page: Int
    ): Observable<PhotoResponse>
}

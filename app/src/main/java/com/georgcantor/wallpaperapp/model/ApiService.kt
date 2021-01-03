package com.georgcantor.wallpaperapp.model

import com.georgcantor.wallpaperapp.BuildConfig.PIXABAY_KEY
import com.georgcantor.wallpaperapp.model.response.pixabay.Pic
import com.georgcantor.wallpaperapp.model.response.unsplash.UnsplashResponse
import com.georgcantor.wallpaperapp.model.response.videos.VideoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("?key=$PIXABAY_KEY")
    suspend fun getPixabayPictures(
        @Query("q") query: String,
        @Query("page") index: Int
    ): Response<Pic>

    @GET
    suspend fun getUnsplashPictures(
        @Url url: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<UnsplashResponse>

    @GET
    suspend fun getVideos(
        @Url url: String,
        @Query("playlistId") playlistId: String
    ): Response<VideoResponse>
}
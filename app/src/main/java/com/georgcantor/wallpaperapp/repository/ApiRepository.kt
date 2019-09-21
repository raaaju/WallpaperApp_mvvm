package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.model.abyss.AbyssResponse
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.model.unsplash.UnsplashResponse
import io.reactivex.Observable

class ApiRepository(private val apiService: ApiService) {

    fun getPixabayPictures(request: String, index: Int): Observable<Pic> =
        apiService.getPixabayPictures(request, index)

    fun getUnsplashPictures(query: String, page: Int): Observable<UnsplashResponse> =
            apiService.getUnsplashPictures(
                    BuildConfig.UNSPLASH_URL,
                    query,
                    page
            )

    fun getAbyssPictures(query: String, page: Int): Observable<AbyssResponse> =
            apiService.getAbyssPictures(
                    BuildConfig.ABYSS_URL,
                    query,
                    page
            )

}
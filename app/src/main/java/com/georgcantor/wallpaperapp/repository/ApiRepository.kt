package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.model.unsplash.UnsplashResponse
import io.reactivex.Observable

class ApiRepository(private val apiService: ApiService) {

    fun getPictures(request: String, index: Int): Observable<Pic> =
        apiService.getPictures(request, index)

    fun getUnsplashPictures(query: String, page: Int): Observable<UnsplashResponse> =
            apiService.getUnsplashPictures(
                    BuildConfig.UNSPLASH_URL,
                    query,
                    page
            )

}
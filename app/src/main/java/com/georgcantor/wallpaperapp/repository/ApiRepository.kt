package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.model.unsplash.UnsplashResponse
import io.reactivex.Observable

class ApiRepository(private val apiService: ApiService) {

    fun getPictures(request: String, index: Int): Observable<Pic> =
        apiService.getPictures(request, index)

    fun getBmw(page: Int): Observable<UnsplashResponse> =
        apiService.getBmw(
            "https://api.unsplash.com/search/photos/?client_id=814e0183c60488e937f966c79d38a31401fcceb515aeee2448292a301665761e&query=bmw",
            page
        )

}
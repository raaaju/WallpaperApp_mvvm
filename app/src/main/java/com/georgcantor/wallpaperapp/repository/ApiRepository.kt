package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.model.remote.ApiService
import io.reactivex.Observable

class ApiRepository(private val apiService: ApiService) {

    fun getPictures(request: String, index: Int): Observable<Pic> =
            apiService.getPictures(request, index)

}
package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.model.ApiService

class Repository(private val service: ApiService) {

    suspend fun getPixabayPictures(
        query: String,
        page: Int
    ) = service.getPixabayPictures(query, page)
}
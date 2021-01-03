package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.BuildConfig.UNSPLASH_URL
import com.georgcantor.wallpaperapp.model.ApiService
import com.georgcantor.wallpaperapp.model.response.CommonPic

class Repository(private val service: ApiService) {

    suspend fun getPictures(
        query: String,
        page: Int
    ): List<CommonPic> {

        val respUnsplash = service.getUnsplashPictures(UNSPLASH_URL, query, page)
        val respPixabay = service.getPixabayPictures(query, page)

        val listUnsplash = respUnsplash.body()?.results?.map {
            CommonPic(
                it.urls?.small,
                it.width ?: 0,
                it.height ?: 0,
                it.altDescription,
                it.urls?.full,
                it.urls?.regular,
                it.hashCode(),
                ""
            )
        }

        val listPixabay = respPixabay.body()?.hits?.map {
            CommonPic(
                url = it.webformatURL,
                width = it.imageWidth,
                heght = it.imageHeight,
                tags = it.tags,
                imageURL = it.imageURL,
                fullHDURL = it.webformatURL,
                id = it.id,
                videoId = ""
            )
        }

        val list = mutableListOf<CommonPic>()
        listUnsplash?.let { list.addAll(it) }
        listPixabay?.let { list.addAll(it) }

        return list
    }
}
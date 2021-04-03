package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.BuildConfig.UNSPLASH_URL
import com.georgcantor.wallpaperapp.BuildConfig.YOUTUBE_URL
import com.georgcantor.wallpaperapp.model.local.FavDao
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.util.Constants.VIDEOS

class Repository(
    private val service: ApiService,
    private val dao: FavDao
) {

    suspend fun getPictures(
        query: String,
        page: Int
    ): List<CommonPic> {

        val respUnsplash = service.getUnsplashPictures(UNSPLASH_URL, query, page)
        val respPixabay = service.getPixabayPictures(query, page)

        val listUnsplash = respUnsplash.body()?.results?.map {
            CommonPic(
                url = it.urls?.small ?: "",
                width = it.width ?: 0,
                height = it.height ?: 0,
                tags = it.altDescription,
                imageURL = it.urls?.full,
                fullHDURL = it.urls?.regular,
                id = it.hashCode(),
                videoId = ""
            )
        }

        val listPixabay = respPixabay.body()?.hits?.map {
            CommonPic(
                url = it.webformatURL ?: "",
                width = it.imageWidth,
                height = it.imageHeight,
                tags = it.tags,
                imageURL = it.imageURL,
                fullHDURL = it.webformatURL,
                id = it.id,
                videoId = ""
            )
        }

        val list = mutableListOf<CommonPic>()
        listUnsplash?.let { list.addAll(it) }
        listPixabay?.let { it.forEach { if (it.id != 158703 && it.id != 158704) list.add(it) } }

        return list.shuffled()
    }

    suspend fun getVideos() = service.getVideos(YOUTUBE_URL, VIDEOS)

    suspend fun addToFavorites(favorite: Favorite) = dao.insert(favorite)

    suspend fun removeFromFavorites(url: String?) = dao.deleteByUrl(url)

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun getByUrl(url: String) = dao.getByUrl(url)

    suspend fun getFavorites() = dao.getAll()

    suspend fun isFavorite(url: String?) = dao.isFavorite(url)
}
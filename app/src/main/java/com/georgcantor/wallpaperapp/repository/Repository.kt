package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.BuildConfig.UNSPLASH_URL
import com.georgcantor.wallpaperapp.BuildConfig.YOUTUBE_URL
import com.georgcantor.wallpaperapp.model.local.FavDao
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.util.Constants.VIDEOS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val service: ApiService,
    private val dao: FavDao
) {

    suspend fun getPictures(query: String, page: Int): List<CommonPic> {
        val list = mutableListOf<CommonPic>()

        withContext(Dispatchers.IO) {
            val respUnsplash = service.getUnsplashPictures(UNSPLASH_URL, query, page)
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
            listUnsplash?.let { list.addAll(it) }
        }

        withContext(Dispatchers.IO) {
            val respPixabay = service.getPixabayPictures(query, page)
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
            listPixabay?.let { list.addAll(it) }
        }

        return list
    }

    suspend fun getVideos() = service.getVideos(YOUTUBE_URL, VIDEOS)

    suspend fun addToFavorites(favorite: Favorite) = dao.insert(favorite)

    suspend fun removeFromFavorites(url: String?) = dao.deleteByUrl(url)

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun getByUrl(url: String) = dao.getByUrl(url)

    suspend fun getFavorites() = dao.getAll()

    suspend fun isFavorite(url: String?) = dao.isFavorite(url)
}
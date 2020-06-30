package com.georgcantor.wallpaperapp.repository

import android.os.Build
import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.BuildConfig.YOUTUBE_URL
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.util.applySchedulers
import io.reactivex.Observable

class ApiRepository(private val apiService: ApiService) {

    fun getPixabayPictures(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        val pictures = ArrayList<CommonPic>()

        return apiService.getPixabayPictures(request, index)
            .flatMap { pic ->
                Observable.fromCallable {
                    pic.hits.map { hit ->
                        pictures.add(
                            CommonPic(
                                hit.webformatURL,
                                hit.imageWidth,
                                hit.imageHeight,
                                hit.tags,
                                hit.imageURL,
                                hit.webformatURL,
                                hit.id,
                                ""
                            )
                        )
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pictures.removeIf {
                            it.id == 158703 || it.id == 158704
                        }
                    }
                    pictures.shuffle()
                    pictures
                }
            }
            .applySchedulers()
    }

    fun getUnsplashPictures(query: String, page: Int): Observable<ArrayList<CommonPic>> {
        val pictures = ArrayList<CommonPic>()

        return apiService.getUnsplashPictures(
            BuildConfig.UNSPLASH_URL,
            query,
            page
        )
            .flatMap { response ->
                Observable.fromCallable {
                    response.results?.map {
                        it.urls.takeUnless { urls ->
                            pictures.add(
                                CommonPic(
                                    urls?.small,
                                    it.width ?: 0,
                                    it.height ?: 0,
                                    it.altDescription,
                                    urls?.full,
                                    urls?.regular,
                                    it.hashCode(),
                                    ""
                                )
                            )
                        }
                    }
                    pictures.shuffle()
                    pictures
                }
            }
            .applySchedulers()
    }

    fun getCategories(request: String): Observable<String> {
        return apiService.getPixabayPictures(request, 1)
            .flatMap {
                Observable.fromCallable(it.hits.first()::webformatURL)
                    .applySchedulers()
            }
    }

    fun getVideos(playlistId: String) = apiService.getVideos(YOUTUBE_URL, playlistId)
}
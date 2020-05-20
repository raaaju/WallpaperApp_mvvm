package com.georgcantor.wallpaperapp.repository

import android.content.Context
import android.os.Build
import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.util.applySchedulers
import io.reactivex.Observable

class ApiRepository(
    private val context: Context,
    private val apiService: ApiService
) {

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
                                hit.favorites,
                                hit.tags,
                                hit.downloads,
                                hit.imageURL,
                                hit.webformatURL,
                                hit.user,
                                hit.id,
                                hit.userImageURL
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
                                    it.likes ?: (10..1000).random(),
                                    "",
                                    (100..10000).random(),
                                    urls?.full,
                                    urls?.regular,
                                    context.getString(R.string.user_unsplash),
                                    it.hashCode(),
                                    urls?.thumb
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

    fun getWallhavenPictures(query: String, page: Int): Observable<ArrayList<CommonPic>> {
        val pictures = ArrayList<CommonPic>()

        return apiService.getWallhavenPictures(BuildConfig.WALLHAVEN_URL, query, page)
            .flatMap { response ->
                Observable.fromCallable {
                    response.data?.map {
                        pictures.add(
                            CommonPic(
                                it.thumbs?.original,
                                it.dimensionX ?: 0,
                                it.dimensionY ?: 0,
                                it.favorites ?: 0,
                                it.category,
                                it.views ?: 0,
                                it.path,
                                it.path,
                                it.source,
                                it.hashCode(),
                                it.thumbs?.small
                            )
                        )
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
}
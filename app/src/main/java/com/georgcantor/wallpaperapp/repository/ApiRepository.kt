package com.georgcantor.wallpaperapp.repository

import android.os.Build
import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.remote.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
                                hit.likes,
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
                            it.imageURL == "https://pixabay.com/get/57e5dd444a51b114a6d1857ace2e357a083edbe252587848722872.png"
                        }
                        pictures.removeIf {
                            it.imageURL == "https://pixabay.com/get/57e5dd444a56b114a6d1857ace2e357a083edbe252587848722872.png"
                        }
                    }
                    pictures.shuffle()
                    pictures
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
                                    it.likes ?: 365,
                                    542,
                                    "car, auto",
                                    5923,
                                    urls?.full,
                                    urls?.regular,
                                    "George Smith",
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAbyssPictures(query: String, page: Int): Observable<ArrayList<CommonPic>> {
        val pictures = ArrayList<CommonPic>()

        return apiService.getAbyssPictures(
            BuildConfig.ABYSS_URL,
            query,
            page
        )
            .flatMap { response ->
                Observable.fromCallable {
                    response.wallpapers?.map {
                        pictures.add(
                            CommonPic(
                                it.urlThumb,
                                it.width?.toInt() ?: 0,
                                it.height?.toInt() ?: 0,
                                481,
                                542,
                                "car, auto",
                                4245,
                                it.urlImage,
                                it.urlImage,
                                "Mike Antony",
                                it.id?.toInt() ?: it.hashCode(),
                                it.urlThumb
                            )
                        )
                    }
                    pictures.shuffle()
                    pictures
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPexelsPictures(query: String, page: Int): Observable<ArrayList<CommonPic>> {
        val pictures = ArrayList<CommonPic>()

        return apiService.getPexelsPictures(BuildConfig.PEXELS_URL, query, 15, page)
            .flatMap { response ->
                Observable.fromCallable {
                    response.photos?.map {
                        pictures.add(
                            CommonPic(
                                it.src.takeIf { it != null }?.medium,
                                it.width,
                                it.height,
                                217,
                                328,
                                "auto, automobile",
                                3846,
                                it.src.takeIf { it != null }?.original,
                                it.src.takeIf { it != null }?.large,
                                it.photographer,
                                it.id,
                                it.src.takeIf { it != null }?.small
                            )
                        )
                    }
                    pictures.shuffle()
                    pictures
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCategories(request: String): Observable<Category> {
        return apiService.getPixabayPictures(request, 1)
            .flatMap {
                Observable.fromCallable {
                    Category(request, it.hits[0].webformatURL)
                }
            }
    }

}
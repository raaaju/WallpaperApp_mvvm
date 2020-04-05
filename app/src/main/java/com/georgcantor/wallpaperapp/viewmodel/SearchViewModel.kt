package com.georgcantor.wallpaperapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.Mark
import com.georgcantor.wallpaperapp.util.applySchedulers
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import io.reactivex.Observable

class SearchViewModel(
    app: Application,
    private val apiRepository: ApiRepository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val isSearchingActive = MutableLiveData<Boolean>()
    val noInternetShow = MutableLiveData<Boolean>()

    fun getPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return Observable.merge(
            apiRepository.getAbyssPictures(request, index),
            apiRepository.getUnsplashPictures(request, index),
            apiRepository.getPexelsPictures(request, index),
            apiRepository.getPixabayPictures(request, index)
        )
            .doFinally {
                noInternetShow.postValue(!context.isNetworkAvailable())
            }
            .applySchedulers()
    }

    fun getAbyssPictures(abyssRequest: String, index: Int): Observable<ArrayList<CommonPic>> {
        return apiRepository.getAbyssPictures(
            abyssRequest,
            if (abyssRequest == context.getString(R.string.bmw_request)
                || abyssRequest == context.getString(R.string.audi_request)
                || abyssRequest == context.getString(R.string.mercedes_request)
            ) index else 1
        )
            .doFinally {
                noInternetShow.postValue(!context.isNetworkAvailable())
            }
            .applySchedulers()
    }

    fun getPicsExceptPexelsUnsplash(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return Observable.merge(
            apiRepository.getAbyssPictures(request, index),
            apiRepository.getPixabayPictures(request, index)
        )
            .doFinally {
                noInternetShow.postValue(!context.isNetworkAvailable())
            }
            .applySchedulers()
    }

    fun searchPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        isSearchingActive.value = true
        return apiRepository.getPixabayPictures(request, index)
            .applySchedulers()
    }

    fun getAbyssRequest(index: Int, mark: Mark): String {
        when (mark) {
            Mark.BMW -> {
                return try {
                    val requests = arrayListOf(
                        "BMW m8",
                        "BMW m5",
                        "BMW x6",
                        "BMW x5",
                        "BMW x7",
                        "BMW 7",
                        "BMW concept",
                        "BMW 5",
                        "BMW m4",
                        "BMW m3",
                        "BMW m2",
                        "BMW 3",
                        "BMW x4",
                        "BMW 6",
                        "BMW M",
                        "BMW x3"
                    )
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.bmw_request)
                }
            }
            Mark.AUDI -> {
                return try {
                    val requests = arrayListOf(
                        "Audi q8",
                        "Audi s8",
                        "Audi r8",
                        "Audi s5",
                        "Audi concept",
                        "Audi 7",
                        "Audi tt",
                        "Audi a4"
                    )
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.audi_request)
                }
            }
            Mark.MERCEDES -> {
                return try {
                    val requests = arrayListOf(
                        "mercedes-benz amg",
                        "mercedes-benz s",
                        "mercedes-benz g",
                        "mercedes-benz e",
                        "mercedes-benz c",
                        "mercedes-benz concept",
                        "mercedes-benz ml",
                        "mercedes-benz gl",
                        "mercedes-benz gt"
                    )
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.mercedes_request)
                }
            }
        }
    }
}
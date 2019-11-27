package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.Mark
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchViewModel(
        private val apiRepository: ApiRepository,
        private val context: Context
) : ViewModel() {

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
                    if (!context.isNetworkAvailable()) noInternetShow.postValue(true) else noInternetShow.postValue(false)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAbyssPictures(abyssRequest: String, index: Int): Observable<ArrayList<CommonPic>> {
        return apiRepository.getAbyssPictures(
                abyssRequest,
                if (abyssRequest == context.getString(R.string.bmw_request)
                        || abyssRequest == context.getString(R.string.audi_request)
                        || abyssRequest == context.getString(R.string.mercedes_request)) index else 1
        )
                .doFinally {
                    if (!context.isNetworkAvailable()) noInternetShow.postValue(true) else noInternetShow.postValue(false)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPicsExceptPexelsUnsplash(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return Observable.merge(
                apiRepository.getAbyssPictures(request, index),
                apiRepository.getPixabayPictures(request, index)
        )
                .doFinally {
                    if (!context.isNetworkAvailable()) noInternetShow.postValue(true) else noInternetShow.postValue(false)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun searchPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        isSearchingActive.value = true
        return apiRepository.getPixabayPictures(request, index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAbyssRequest(index: Int, mark: Mark): String {
        when (mark) {
            Mark.BMW -> {
                return try {
                    val requests = arrayListOf("BMW m8", "BMW m5", "BMW x6", "BMW x5", "BMW x7", "BMW 7", "BMW concept", "BMW 5", "BMW m4", "BMW m3", "BMW m2", "BMW 3", "BMW x4", "BMW 6", "BMW M", "BMW x3")
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.bmw_request)
                }
            }
            Mark.AUDI -> {
                return try {
                    val requests = arrayListOf("Audi q8", "Audi s8", "Audi r8", "Audi s5", "Audi concept", "Audi 7", "Audi tt", "Audi a4")
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.audi_request)
                }
            }
            Mark.MERCEDES -> {
                return try {
                    val requests = arrayListOf("mercedes-benz amg", "mercedes-benz s", "mercedes-benz g", "mercedes-benz e", "mercedes-benz c", "mercedes-benz concept", "mercedes-benz ml", "mercedes-benz gl", "mercedes-benz gt")
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.mercedes_request)
                }
            }
        }
    }

}
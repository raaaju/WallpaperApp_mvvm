package com.georgcantor.wallpaperapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
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
            apiRepository.getUnsplashPictures(request, index),
            apiRepository.getPexelsPictures(request, index),
            apiRepository.getPixabayPictures(request, index)
        )
            .doFinally {
                noInternetShow.postValue(!context.isNetworkAvailable())
            }
            .applySchedulers()
    }

    fun getPixabayPictures(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return apiRepository.getPixabayPictures(request, index)
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
}
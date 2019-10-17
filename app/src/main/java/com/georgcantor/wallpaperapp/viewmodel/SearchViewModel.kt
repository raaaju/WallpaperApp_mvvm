package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    val isSearchingActive = MutableLiveData<Boolean>()

    fun getPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return Observable.merge(
            apiRepository.getPixabayPictures(request, index),
            apiRepository.getUnsplashPictures(request, index),
            apiRepository.getAbyssPictures(request, index),
            apiRepository.getPexelsPictures(request, index)
        )
    }

    fun searchPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        isSearchingActive.value = true
        return apiRepository.getPixabayPictures(request, index)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}
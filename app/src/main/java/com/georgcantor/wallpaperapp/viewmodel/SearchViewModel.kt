package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.applySchedulers
import io.reactivex.Observable

class SearchViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    val isSearchingActive = MutableLiveData<Boolean>()

    fun getPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return Observable.merge(
            apiRepository.getUnsplashPictures(request, index),
            apiRepository.getPexelsPictures(request, index),
            apiRepository.getPixabayPictures(request, index)
        )
            .applySchedulers()
    }

    fun getPixabayPictures(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return apiRepository.getPixabayPictures(request, index)
            .applySchedulers()
    }

    fun searchPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        isSearchingActive.value = true
        return apiRepository.getPixabayPictures(request, index)
            .applySchedulers()
    }
}
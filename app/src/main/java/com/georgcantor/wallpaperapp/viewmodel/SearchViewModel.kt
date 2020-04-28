package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.applySchedulers
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    private val disposable = CompositeDisposable()

    val isSearchingActive = MutableLiveData<Boolean>()
    val pictures = MutableLiveData<MutableList<CommonPic>>()
    val error = MutableLiveData<String>()

    fun getPictures(request: String, index: Int) {
        disposable.add(
            Observable.merge(
                apiRepository.getUnsplashPictures(request, index),
                apiRepository.getPixabayPictures(request, index)
            )
                .subscribeOn(Schedulers.io())
                .subscribe(pictures::postValue) {
                    error.postValue(it.message)
                }
        )
    }

    fun getPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return Observable.merge(
            apiRepository.getUnsplashPictures(request, index),
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SelectCatViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    fun getPictures(request: String, index: Int): Observable<Pic> =
            apiRepository.getPictures(request, index)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

}
package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.model.PicUrl
import com.georgcantor.wallpaperapp.model.unsplash.Result
import com.georgcantor.wallpaperapp.model.unsplash.UnsplashResponse
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.ui.util.PicturesMapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class SearchViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    fun getPictures(request: String, index: Int): Observable<Pic> =
        apiRepository.getPictures(request, index)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getPics(request: String, index: Int): Observable<ArrayList<PicUrl>> {
        return Observable.combineLatest<List<Hit>, List<Result>, ArrayList<PicUrl>>(
            apiRepository.getPictures(request, index).map { it.hits },
                apiRepository.getUnsplashPictures(request,index).map(UnsplashResponse::results), BiFunction(
                PicturesMapper.Companion::mergeResponses
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}
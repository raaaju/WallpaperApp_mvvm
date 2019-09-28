package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.abyss.AbyssResponse
import com.georgcantor.wallpaperapp.model.abyss.Wallpaper
import com.georgcantor.wallpaperapp.model.unsplash.Result
import com.georgcantor.wallpaperapp.model.unsplash.UnsplashResponse
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.ui.util.PicturesMapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

class SearchViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    val isSearchingActive = MutableLiveData<Boolean>()

    fun getPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        return Observable.combineLatest<List<Hit>, List<Result>, List<Wallpaper>, ArrayList<CommonPic>>(
            apiRepository.getPixabayPictures(request, index).map { it.hits },
                apiRepository.getUnsplashPictures(request,index).map(UnsplashResponse::results),
                apiRepository.getAbyssPictures(request,index).map(AbyssResponse::wallpapers),
                Function3(
                PicturesMapper.Companion::mergeResponses
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun searchPics(request: String, index: Int): Observable<ArrayList<CommonPic>> {
        isSearchingActive.value = true
        return apiRepository.getPixabayPictures(request, index)
            .map {
                PicturesMapper.convertResponse(it.hits)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}
package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.PicUrl
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

    fun getPics(request: String, index: Int): Observable<ArrayList<PicUrl>> {
        return Observable.combineLatest<List<Hit>, List<Result>, List<Wallpaper>, ArrayList<PicUrl>>(
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

}
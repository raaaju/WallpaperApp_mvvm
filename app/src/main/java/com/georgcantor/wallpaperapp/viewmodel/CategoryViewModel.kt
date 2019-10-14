package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.model.data.pixabay.Hit
import com.georgcantor.wallpaperapp.model.data.unsplash.UnsplashResponse
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.PicturesMapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function8
import io.reactivex.schedulers.Schedulers

class CategoryViewModel(private val context: Context,
                        private val apiRepository: ApiRepository) : ViewModel() {

    fun getAllCategories(): Observable<List<Category>> {
        return Observable.combineLatest<List<Category>, List<Category>, List<Category>>(
                getCategories(),
                getCategories2(),
                BiFunction(
                        PicturesMapper.Companion::mergeCategories
                )
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getCategories(): Observable<List<Category>> {
        return Observable.combineLatest<List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                 List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                 List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                 List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                 List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                 List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                 List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                 List<com.georgcantor.wallpaperapp.model.data.unsplash.Result>,
                List<Category>>(
                apiRepository.getUnsplashPictures(context.getString(R.string.animals), 1).map(UnsplashResponse::results),
                apiRepository.getUnsplashPictures(context.getString(R.string.textures), 1).map(UnsplashResponse::results),
                apiRepository.getUnsplashPictures(context.getString(R.string.buildings), 1).map(UnsplashResponse::results),
                apiRepository.getUnsplashPictures(context.getString(R.string.nature), 1).map(UnsplashResponse::results),
                apiRepository.getUnsplashPictures(context.getString(R.string.music), 1).map(UnsplashResponse::results),
                apiRepository.getUnsplashPictures(context.getString(R.string.travel), 1).map(UnsplashResponse::results),
                apiRepository.getUnsplashPictures(context.getString(R.string.business), 1).map(UnsplashResponse::results),
                apiRepository.getUnsplashPictures(context.getString(R.string.fashion), 1).map(UnsplashResponse::results),
                Function8(PicturesMapper.Companion::mergeCategories)
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getCategories2(): Observable<List<Category>> {
        return Observable.combineLatest<List<Hit>,
                List<Hit>,
                List<Hit>,
                List<Hit>,
                List<Hit>,
                List<Hit>,
                List<Hit>,
                List<Hit>,
                List<Category>>(
                apiRepository.getPixabayPictures(context.getString(R.string.computer), 1).map { it.hits },
                apiRepository.getPixabayPictures(context.getString(R.string.feelings), 1).map { it.hits },
                apiRepository.getPixabayPictures(context.getString(R.string.food), 1).map { it.hits },
                apiRepository.getPixabayPictures(context.getString(R.string.health), 1).map { it.hits },
                apiRepository.getPixabayPictures(context.getString(R.string.people), 1).map { it.hits },
                apiRepository.getPixabayPictures(context.getString(R.string.places), 1).map { it.hits },
                apiRepository.getPixabayPictures(context.getString(R.string.science), 1).map { it.hits },
                apiRepository.getPixabayPictures(context.getString(R.string.sports), 1).map { it.hits },
                Function8(PicturesMapper.Companion::mergeCategories2)
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}
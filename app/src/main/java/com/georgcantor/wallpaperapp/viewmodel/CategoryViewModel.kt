package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.repository.ApiRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CategoryViewModel(private val context: Context,
                        private val apiRepository: ApiRepository) : ViewModel() {

    fun getCategories(): Observable<ArrayList<Category>> {
        return Observable.fromCallable {
            val list = ArrayList<Category>()
            apiRepository.getCategories(context.getString(R.string.animals)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.buildings)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.computer)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.education)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.health)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.fashion)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.feelings)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.food)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.music)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.nature)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.people)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.places)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.science)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.sports)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.textures)).subscribe ({ list.add(it) }, {})
            apiRepository.getCategories(context.getString(R.string.travel)).subscribe ({ list.add(it) }, {})
            list
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}
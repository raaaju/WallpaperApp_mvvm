package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.ui.fragment.CategoryFragment.Companion.CATEGORIES
import com.georgcantor.wallpaperapp.util.PreferenceManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CategoryViewModel(
    private val context: Context,
    private val apiRepository: ApiRepository
) : ViewModel() {

    fun getCategories(preferenceManager: PreferenceManager): Observable<ArrayList<String>?> {
        return Observable.fromCallable {
            val list = ArrayList<String>()
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
            val urls = ArrayList<String>()
            list.map(urls::add)
            preferenceManager.saveCategories(CATEGORIES, urls)
            list
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSavedCategories(preferenceManager: PreferenceManager): Observable<ArrayList<String>?> {
        return Observable.fromCallable {
            val savedUrls = preferenceManager.getCategories(CATEGORIES)
            savedUrls
        }
    }

    fun getCategoryNames(): Observable<ArrayList<String>> {
        return Observable.fromCallable {
            val names = ArrayList<String>()
            names.add(context.getString(R.string.animals))
            names.add(context.getString(R.string.buildings))
            names.add(context.getString(R.string.computer))
            names.add(context.getString(R.string.education))
            names.add(context.getString(R.string.health))
            names.add(context.getString(R.string.fashion))
            names.add(context.getString(R.string.feelings))
            names.add(context.getString(R.string.food))
            names.add(context.getString(R.string.music))
            names.add(context.getString(R.string.nature))
            names.add(context.getString(R.string.people))
            names.add(context.getString(R.string.places))
            names.add(context.getString(R.string.science))
            names.add(context.getString(R.string.sports))
            names.add(context.getString(R.string.textures))
            names.add(context.getString(R.string.travel))
            names
        }
    }

}
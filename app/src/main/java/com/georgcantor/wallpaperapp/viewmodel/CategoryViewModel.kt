package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
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

    fun getCategories(preferenceManager: PreferenceManager): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            val list = ArrayList<Category>()
            apiRepository.getCategories(context.getString(R.string.animals)).subscribe ({
                list.add(Category(context.getString(R.string.animals), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.buildings)).subscribe ({
                list.add(Category(context.getString(R.string.buildings), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.computer)).subscribe ({
                list.add(Category(context.getString(R.string.computer), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.education)).subscribe ({
                list.add(Category(context.getString(R.string.education), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.health)).subscribe ({
                list.add(Category(context.getString(R.string.health), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.fashion)).subscribe ({
                list.add(Category(context.getString(R.string.fashion), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.feelings)).subscribe ({
                list.add(Category(context.getString(R.string.feelings), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.food)).subscribe ({
                list.add(Category(context.getString(R.string.food), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.music)).subscribe ({
                list.add(Category(context.getString(R.string.music), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.nature)).subscribe ({
                list.add(Category(context.getString(R.string.nature), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.people)).subscribe ({
                list.add(Category(context.getString(R.string.people), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.places)).subscribe ({
                list.add(Category(context.getString(R.string.places), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.science)).subscribe ({
                list.add(Category(context.getString(R.string.science), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.sports)).subscribe ({
                list.add(Category(context.getString(R.string.sports), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.textures)).subscribe ({
                list.add(Category(context.getString(R.string.textures), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.travel)).subscribe ({
                list.add(Category(context.getString(R.string.travel), it)) }, {})
            val urls = ArrayList<Category>()
            list.map {
                urls.add(Category(it.categoryName, it.categoryUrl))
            }
            preferenceManager.saveCategories(CATEGORIES, urls)
            list
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSavedCategories(preferenceManager: PreferenceManager): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            val savedUrls = preferenceManager.getCategories()
            savedUrls
        }
    }

}
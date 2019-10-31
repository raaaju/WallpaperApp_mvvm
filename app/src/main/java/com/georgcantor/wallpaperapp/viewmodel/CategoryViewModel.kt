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
                list.add(Category(context.getString(R.string.Animals), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.buildings)).subscribe ({
                list.add(Category(context.getString(R.string.Buildings), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.computer)).subscribe ({
                list.add(Category(context.getString(R.string.Computer), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.education)).subscribe ({
                list.add(Category(context.getString(R.string.Education), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.health)).subscribe ({
                list.add(Category(context.getString(R.string.Health), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.fashion)).subscribe ({
                list.add(Category(context.getString(R.string.Fashion), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.feelings)).subscribe ({
                list.add(Category(context.getString(R.string.Feelings), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.food)).subscribe ({
                list.add(Category(context.getString(R.string.Food), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.music)).subscribe ({
                list.add(Category(context.getString(R.string.Music), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.nature)).subscribe ({
                list.add(Category(context.getString(R.string.Nature), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.people)).subscribe ({
                list.add(Category(context.getString(R.string.People), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.places)).subscribe ({
                list.add(Category(context.getString(R.string.Places), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.science)).subscribe ({
                list.add(Category(context.getString(R.string.Science), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.sports)).subscribe ({
                list.add(Category(context.getString(R.string.Sports), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.textures)).subscribe ({
                list.add(Category(context.getString(R.string.Textures), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.travel)).subscribe ({
                list.add(Category(context.getString(R.string.Travel), it)) }, {})
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
            val savedUrls = preferenceManager.getCategories(CATEGORIES)
            savedUrls
        }
    }

}
package com.georgcantor.wallpaperapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.Constants.CATEGORIES
import com.georgcantor.wallpaperapp.util.Constants.IS_RATING_EXIST
import com.georgcantor.wallpaperapp.util.Constants.LAUNCHES
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainViewModel(
    app: Application,
    private val apiRepository: ApiRepository,
    private val prefManager: PreferenceManager
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val isGalleryVisible = MutableLiveData<Boolean>()
    val isRateDialogShow = MutableLiveData<Boolean>()

    fun loadCategories() {
        if (prefManager.getCategories(CATEGORIES)?.size ?: 0 > 13) return

        Observable.fromCallable {
            isGalleryVisible.postValue(false)

            val categories = ArrayList<Category>()
            with(apiRepository) {
                getCategories(context.getString(R.string.animals)).subscribe({
                    categories.add(Category(context.getString(R.string.Animals), it)) }, {})
                getCategories(context.getString(R.string.buildings)).subscribe({
                    categories.add(Category(context.getString(R.string.Buildings), it)) }, {})
                getCategories(context.getString(R.string.nature)).subscribe({
                    categories.add(Category(context.getString(R.string.Nature), it)) }, {})
                getCategories(context.getString(R.string.textures)).subscribe({
                    categories.add(Category(context.getString(R.string.Textures), it)) }, {})
                getCategories(context.getString(R.string.travel)).subscribe({
                    categories.add(Category(context.getString(R.string.Travel), it)) }, {})
                getCategories(context.getString(R.string.places)).subscribe({
                    categories.add(Category(context.getString(R.string.Places), it)) }, {})
                getCategories(context.getString(R.string.music)).subscribe({
                    categories.add(Category(context.getString(R.string.Music), it)) }, {})
                getCategories(context.getString(R.string.health)).subscribe({
                    categories.add(Category(context.getString(R.string.Health), it)) }, {})
                getCategories(context.getString(R.string.fashion)).subscribe({
                    categories.add(Category(context.getString(R.string.Fashion), it)) }, {})
                getCategories(context.getString(R.string.feelings)).subscribe({
                    categories.add(Category(context.getString(R.string.Feelings), it)) }, {})
                getCategories(context.getString(R.string.food)).subscribe({
                    categories.add(Category(context.getString(R.string.Food), it)) }, {})
                getCategories(context.getString(R.string.people)).subscribe({
                    categories.add(Category(context.getString(R.string.People), it)) }, {})
                getCategories(context.getString(R.string.science)).subscribe({
                    categories.add(Category(context.getString(R.string.Science), it)) }, {})
                getCategories(context.getString(R.string.sports)).subscribe({
                    categories.add(Category(context.getString(R.string.Sports), it)) }, {})
                getCategories(context.getString(R.string.computer)).subscribe({
                    categories.add(Category(context.getString(R.string.Computer), it)) }, {})
                getCategories(context.getString(R.string.education)).subscribe({
                    categories.add(Category(context.getString(R.string.Education), it)) }, {})
            }
            if (categories.size % 2 != 0) categories.removeAt(categories.size - 1)

            prefManager.saveCategories(CATEGORIES, categories)
            if (context.isNetworkAvailable()) isGalleryVisible.postValue(true)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun checkNumberOfLaunches() {
        var numberOfLaunches = prefManager.getInt(LAUNCHES)
        if (numberOfLaunches < 4) {
            numberOfLaunches++
            prefManager.saveInt(LAUNCHES, numberOfLaunches)
            if (numberOfLaunches > 3 && !prefManager.getBoolean(IS_RATING_EXIST)) {
                isRateDialogShow.value = true
            }
        }
    }
}
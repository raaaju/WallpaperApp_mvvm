package com.georgcantor.wallpaperapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.Constants.Companion.CATEGORIES
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import io.reactivex.Observable

class CategoryViewModel(
    app: Application,
    private val preferenceManager: PreferenceManager
) : AndroidViewModel(app) {

    val noInternetShow = MutableLiveData<Boolean>()

    fun getSavedCategories(): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            noInternetShow.postValue(!getApplication<MyApplication>().isNetworkAvailable())
            preferenceManager.getCategories(CATEGORIES)
        }
    }
}
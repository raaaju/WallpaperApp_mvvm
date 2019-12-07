package com.georgcantor.wallpaperapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import com.georgcantor.wallpaperapp.view.fragment.CategoryFragment.Companion.CATEGORIES
import io.reactivex.Observable

class CategoryViewModel(
        app: Application,
        private val preferenceManager: PreferenceManager
) : AndroidViewModel(app) {

    val noInternetShow = MutableLiveData<Boolean>()

    fun getSavedCategories(): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            if (!getApplication<MyApplication>().isNetworkAvailable()) {
                noInternetShow.postValue(true)
            } else {
                noInternetShow.postValue(false)
            }
            preferenceManager.getCategories(CATEGORIES)
        }
    }

}
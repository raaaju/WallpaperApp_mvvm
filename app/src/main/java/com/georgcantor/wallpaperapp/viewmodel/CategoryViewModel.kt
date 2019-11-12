package com.georgcantor.wallpaperapp.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.view.fragment.CategoryFragment.Companion.CATEGORIES
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import io.reactivex.Observable

class CategoryViewModel : ViewModel() {

    val noInternetShow = MutableLiveData<Boolean>()

    fun getSavedCategories(
        preferenceManager: PreferenceManager,
        activity: Activity
    ): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            if (!activity.isNetworkAvailable()) noInternetShow.postValue(true) else noInternetShow.postValue(false)
            preferenceManager.getCategories(CATEGORIES)
        }
    }

}
package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.view.fragment.CategoryFragment.Companion.CATEGORIES
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import io.reactivex.Observable

class CategoryViewModel(private val context: Context) : ViewModel() {

    val noInternetShow = MutableLiveData<Boolean>()

    fun getSavedCategories(preferenceManager: PreferenceManager): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            if (!context.isNetworkAvailable()) noInternetShow.postValue(true) else noInternetShow.postValue(false)
            preferenceManager.getCategories(CATEGORIES)
        }
    }

}
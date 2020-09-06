package com.georgcantor.wallpaperapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.georgcantor.wallpaperapp.util.Constants.CATEGORIES
import com.georgcantor.wallpaperapp.util.PreferenceManager
import io.reactivex.Observable

class CategoryViewModel(
    app: Application,
    private val preferenceManager: PreferenceManager
) : AndroidViewModel(app) {

    fun getSavedCategories() = Observable.fromCallable {
        preferenceManager.getCategories(CATEGORIES)
    }
}
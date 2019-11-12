package com.georgcantor.wallpaperapp.viewmodel

import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.view.fragment.CategoryFragment.Companion.CATEGORIES
import com.georgcantor.wallpaperapp.util.PreferenceManager
import io.reactivex.Observable

class CategoryViewModel : ViewModel() {

    fun getSavedCategories(preferenceManager: PreferenceManager): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            preferenceManager.getCategories(CATEGORIES)
        }
    }

}
package com.georgcantor.wallpaperapp.ui.activity.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.response.Category
import com.georgcantor.wallpaperapp.repository.Repository
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    val categories = MutableLiveData<List<Category>>()

    init {
        viewModelScope.launch {
            val names = app.baseContext.resources.getStringArray(R.array.categories_array)
            val pictures = mutableListOf<Category>()

            val pic1 = repository.getPictures(names[0], 1)[0]
            val pic2 = repository.getPictures(names[1], 1)[0]
            val pic3 = repository.getPictures(names[2], 1)[0]
            val pic4 = repository.getPictures(names[3], 1)[0]
            val pic5 = repository.getPictures(names[4], 1)[0]
            val pic6 = repository.getPictures(names[5], 1)[0]

            pictures.add(Category(names[0], pic1.url ?: ""))
            pictures.add(Category(names[1], pic2.url ?: ""))
            pictures.add(Category(names[2], pic3.url ?: ""))
            pictures.add(Category(names[3], pic4.url ?: ""))
            pictures.add(Category(names[4], pic5.url ?: ""))
            pictures.add(Category(names[5], pic6.url ?: ""))

            categories.postValue(pictures)
        }
    }
}
package com.georgcantor.wallpaperapp.ui.activity.categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.model.response.Category
import com.georgcantor.wallpaperapp.repository.Repository
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: Repository) : ViewModel() {

    val categories = MutableLiveData<List<Category>>()

    init {
        viewModelScope.launch {
            val names = listOf("art", "sport", "music", "nature")
            val pictures = mutableListOf<Category>()

            val pic1 = repository.getPictures("art", 1)[0]
            val pic2 = repository.getPictures("sport", 1)[0]
            val pic3 = repository.getPictures("music", 1)[0]
            val pic4 = repository.getPictures("nature", 1)[0]

            pictures.add(Category("art", pic1.url ?: ""))
            pictures.add(Category("sport", pic2.url ?: ""))
            pictures.add(Category("music", pic3.url ?: ""))
            pictures.add(Category("nature", pic4.url ?: ""))

            categories.postValue(pictures)
        }
    }
}
package com.georgcantor.wallpaperapp.ui.activity.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.remote.response.Category
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    val categories = MutableLiveData<List<Category>>()
    val progressIsVisible = MutableLiveData<Boolean>().apply { value = true }

    init {
        if (app.baseContext.isNetworkAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {
                val names = app.baseContext.resources.getStringArray(R.array.categories_array)
                val pictures = mutableListOf<Category>()

                with(repository) {
                    async { getPictures(names[0], 1)[0].apply { pictures.add(Category(names[0], url)) } }
                    async { getPictures(names[1], 1)[0].apply { pictures.add(Category(names[1], url)) } }
                    async { getPictures(names[2], 1)[0].apply { pictures.add(Category(names[2], url)) } }
                    async { getPictures(names[3], 1)[0].apply { pictures.add(Category(names[3], url)) } }
                    async { getPictures(names[4], 1)[0].apply { pictures.add(Category(names[4], url)) } }
                    async { getPictures(names[5], 1)[0].apply { pictures.add(Category(names[5], url)) } }
                    async { getPictures(names[6], 1)[0].apply { pictures.add(Category(names[6], url)) } }
                    async { getPictures(names[7], 1)[0].apply { pictures.add(Category(names[7], url)) } }
                    async { getPictures(names[8], 1)[0].apply { pictures.add(Category(names[8], url)) } }
                    async { getPictures(names[9], 1)[0].apply { pictures.add(Category(names[9], url)) } }
                    async { getPictures(names[10], 1)[0].apply { pictures.add(Category(names[10], url)) } }
                    async { getPictures(names[11], 1)[0].apply { pictures.add(Category(names[11], url)) } }
                }

                categories.postValue(pictures)
                progressIsVisible.postValue(false)
            }
        }
    }
}
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
                val list = mutableListOf<Category>()

                with(repository) {
                    async { getPics(names.getOrNull(0).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[0], url)) } }
                    async { getPics(names.getOrNull(1).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[1], url)) } }
                    async { getPics(names.getOrNull(2).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[2], url)) } }
                    async { getPics(names.getOrNull(3).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[3], url)) } }
                    async { getPics(names.getOrNull(4).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[4], url)) } }
                    async { getPics(names.getOrNull(5).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[5], url)) } }
                    async { getPics(names.getOrNull(6).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[6], url)) } }
                    async { getPics(names.getOrNull(7).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[7], url)) } }
                    async { getPics(names.getOrNull(8).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[8], url)) } }
                    async { getPics(names.getOrNull(9).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[9], url)) } }
                    async { getPics(names.getOrNull(10).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[10], url)) } }
                    async { getPics(names.getOrNull(11).orEmpty(), 1).getOrNull(0)?.apply { list.add(Category(names[11], url)) } }
                }

                categories.postValue(list)
                progressIsVisible.postValue(false)
            }
        }
    }
}
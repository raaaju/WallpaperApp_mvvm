package com.georgcantor.wallpaperapp.ui.activity.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.repository.Repository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: Repository) : ViewModel() {

    val isFavorite = MutableLiveData<Boolean>()

    fun isFavorite(url: String?) {
        viewModelScope.launch {
            isFavorite.postValue(repository.isFavorite(url))
        }
    }

    fun addOrRemoveFromFavorites(pic: CommonPic?) {
        viewModelScope.launch {
            when (isFavorite.value) {
                true -> {
                    repository.removeFromFavorites(pic?.url)
                    isFavorite.postValue(false)
                }
                false -> {
                    repository.addToFavorites(Favorite(pic?.url ?: "", Gson().toJson(pic)))
                    isFavorite.postValue(true)
                }
            }
        }
    }
}
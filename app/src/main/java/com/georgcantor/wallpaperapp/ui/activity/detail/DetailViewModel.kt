package com.georgcantor.wallpaperapp.ui.activity.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.repository.Repository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: Repository) : ViewModel() {

    fun addToFavorites(pic: CommonPic?) {
        viewModelScope.launch {
            repository.addToFavorites(Favorite(pic?.url ?: "", Gson().toJson(pic)))
        }
    }
}
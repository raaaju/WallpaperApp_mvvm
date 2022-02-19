package com.georgcantor.wallpaperapp.ui.activity.videos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.model.remote.response.videos.Item
import com.georgcantor.wallpaperapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val videos = MutableLiveData<List<Item>>()

    init {
        viewModelScope.launch {
            videos.postValue(repository.getVideos().body()?.items)
        }
    }
}
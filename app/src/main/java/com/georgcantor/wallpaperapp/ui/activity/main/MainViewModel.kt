package com.georgcantor.wallpaperapp.ui.activity.main

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.util.Constants.LAUNCHES
import com.georgcantor.wallpaperapp.util.getAny
import com.georgcantor.wallpaperapp.util.putAny
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: SharedPreferences) : ViewModel() {

    val isRateDialogShow = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            var numberOfLaunches = preferences.getAny(0, LAUNCHES) as Int
            when (numberOfLaunches) {
                in 0..3 -> {
                    numberOfLaunches++
                    preferences.putAny(LAUNCHES, numberOfLaunches)
                }
                else -> isRateDialogShow.postValue(true)
            }
        }
    }
}
package com.georgcantor.wallpaperapp.model.remote.response.pixabay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Pic(
    var totalHits: Int = 0,
    var hits: List<Hit>,
    private var total: Int = 0
) : Parcelable
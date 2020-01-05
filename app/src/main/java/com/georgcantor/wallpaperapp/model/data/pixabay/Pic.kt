package com.georgcantor.wallpaperapp.model.data.pixabay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pic(
        private var totalHits: Int = 0,
        var hits: List<Hit>,
        private var total: Int = 0
) : Parcelable
package com.georgcantor.wallpaperapp.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommonPic(
        val url: String?,
        val width: Int,
        val heght: Int,
        var favorites: Int = 0,
        var tags: String? = null,
        var downloads: Int = 0,
        var imageURL: String? = null,
        var fullHDURL: String? = null,
        var user: String? = null,
        var id: Int = 0,
        var userImageURL: String? = null
) : Parcelable
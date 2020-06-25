package com.georgcantor.wallpaperapp.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommonPic(
    val url: String?,
    val width: Int,
    val heght: Int,
    var tags: String?,
    var imageURL: String?,
    var fullHDURL: String?,
    var id: Int?,
    var videoId: String?
) : Parcelable
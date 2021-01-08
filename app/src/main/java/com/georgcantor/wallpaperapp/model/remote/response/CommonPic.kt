package com.georgcantor.wallpaperapp.model.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonPic(
    val url: String,
    val width: Int,
    val heght: Int,
    var tags: String?,
    var imageURL: String?,
    var fullHDURL: String?,
    var id: Int?,
    var videoId: String?
) : Parcelable
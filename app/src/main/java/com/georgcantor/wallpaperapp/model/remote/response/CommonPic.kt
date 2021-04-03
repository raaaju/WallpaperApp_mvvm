package com.georgcantor.wallpaperapp.model.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CommonPic(
    val url: String,
    val width: Int,
    val height: Int,
    var tags: String?,
    var imageURL: String?,
    var fullHDURL: String?,
    var id: Int?,
    var videoId: String?
) : Parcelable
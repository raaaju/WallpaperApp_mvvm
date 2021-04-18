package com.georgcantor.wallpaperapp.model.remote.response.pixabay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Hit(
    var previewHeight: Int = 0,
    var likes: Int = 0,
    var favorites: Int = 0,
    var tags: String?,
    var webformatHeight: Int = 0,
    var views: Int = 0,
    var webformatWidth: Int = 0,
    var previewWidth: Int = 0,
    var comments: Int = 0,
    var downloads: Int = 0,
    var pageURL: String?,
    var previewURL: String?,
    var webformatURL: String?,
    var imageURL: String?,
    var fullHDURL: String?,
    var imageWidth: Int = 0,
    var userId: Int = 0,
    var user: String?,
    var type: String?,
    var id: Int = 0,
    var userImageURL: String?,
    var imageHeight: Int = 0
) : Parcelable
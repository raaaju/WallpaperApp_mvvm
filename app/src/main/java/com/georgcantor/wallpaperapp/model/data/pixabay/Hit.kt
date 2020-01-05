package com.georgcantor.wallpaperapp.model.data.pixabay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Hit(
        var previewHeight: Int = 0,
        var likes: Int = 0,
        var favorites: Int = 0,
        var tags: String? = null,
        var webformatHeight: Int = 0,
        var views: Int = 0,
        var webformatWidth: Int = 0,
        var previewWidth: Int = 0,
        var comments: Int = 0,
        var downloads: Int = 0,
        var pageURL: String? = null,
        var previewURL: String? = null,
        var webformatURL: String? = null,
        var imageURL: String? = null,
        var fullHDURL: String? = null,
        var imageWidth: Int = 0,
        var userId: Int = 0,
        var user: String? = null,
        var type: String? = null,
        var id: Int = 0,
        var userImageURL: String? = null,
        var imageHeight: Int = 0
) : Parcelable
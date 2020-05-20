package com.georgcantor.wallpaperapp.model.data.wallhaven

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Thumbs {
    @SerializedName("large")
    @Expose
    var large: String? = null

    @SerializedName("original")
    @Expose
    var original: String? = null

    @SerializedName("small")
    @Expose
    var small: String? = null
}
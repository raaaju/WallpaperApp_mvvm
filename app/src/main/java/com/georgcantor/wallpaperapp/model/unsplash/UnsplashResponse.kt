package com.georgcantor.wallpaperapp.model.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UnsplashResponse {

    @SerializedName("total")
    @Expose
    var total: Int? = null
    @SerializedName("total_pages")
    @Expose
    var totalPages: Int? = null
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null

}
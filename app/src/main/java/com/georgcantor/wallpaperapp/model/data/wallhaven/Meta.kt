package com.georgcantor.wallpaperapp.model.data.wallhaven

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Meta {
    @SerializedName("current_page")
    @Expose
    var currentPage: Int? = null

    @SerializedName("last_page")
    @Expose
    var lastPage: Int? = null

    @SerializedName("per_page")
    @Expose
    var perPage: String? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    @SerializedName("query")
    @Expose
    var query: Any? = null

    @SerializedName("seed")
    @Expose
    var seed: Any? = null
}
package com.georgcantor.wallpaperapp.model.data.wallhaven

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class WallhavenResponse {
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    @SerializedName("meta")
    @Expose
    var meta: Meta? = null
}
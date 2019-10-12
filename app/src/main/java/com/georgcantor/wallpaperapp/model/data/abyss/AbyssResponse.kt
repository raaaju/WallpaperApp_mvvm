package com.georgcantor.wallpaperapp.model.data.abyss

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AbyssResponse {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null
    @SerializedName("wallpapers")
    @Expose
    var wallpapers: List<Wallpaper>? = null
    @SerializedName("total_match")
    @Expose
    var totalMatch: String? = null

}
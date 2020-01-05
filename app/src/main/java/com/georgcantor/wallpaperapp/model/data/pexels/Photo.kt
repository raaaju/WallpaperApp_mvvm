package com.georgcantor.wallpaperapp.model.data.pexels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Photo {

    @Expose
    var id: Int = 0
    @Expose
    var width: Int = 0
    @Expose
    var height: Int = 0
    @Expose
    var url: String? = null
    @Expose
    var photographer: String? = null
    @SerializedName("photographer_url")
    @Expose
    var photographerUrl: String? = null
    @SerializedName("photographer_id")
    @Expose
    var photographerId: Int = 0
    @Expose
    var src: Source? = null

}
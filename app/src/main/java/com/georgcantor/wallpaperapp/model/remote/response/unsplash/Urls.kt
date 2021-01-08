package com.georgcantor.wallpaperapp.model.remote.response.unsplash

import com.google.gson.annotations.Expose

class Urls {
    @Expose
    var raw: String? = null

    @Expose
    var full: String? = null

    @Expose
    var regular: String? = null

    @Expose
    var small: String? = null

    @Expose
    var thumb: String? = null
}
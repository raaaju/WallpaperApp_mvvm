package com.georgcantor.wallpaperapp.model.remote

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.model.Pic
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("?key=" + BuildConfig.API_KEY)
    fun getPictures(@Query("q") query: String,
                    @Query("page") index: Int): Observable<Pic>
}

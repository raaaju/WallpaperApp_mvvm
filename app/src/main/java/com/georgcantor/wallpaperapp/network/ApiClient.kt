package com.georgcantor.wallpaperapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://pixabay.com/api/"
    private var retrofit: Retrofit? = null

    fun getClient(httpClient: OkHttpClient.Builder): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
        }

        return retrofit
    }
}

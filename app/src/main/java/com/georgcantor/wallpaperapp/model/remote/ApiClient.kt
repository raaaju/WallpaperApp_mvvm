package com.georgcantor.wallpaperapp.model.remote

import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.MyApplication
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object ApiClient {

    private lateinit var retrofit: Retrofit

    fun create(): ApiService {

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient().newBuilder()
                .addNetworkInterceptor(ResponseCacheInterceptor())
                .addInterceptor(OfflineResponseCacheInterceptor())
                .cache(Cache(File(MyApplication.instance?.cacheDir,
                        "ResponsesCache"), (10 * 1024 * 1024).toLong()))
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    fun get(): ApiService {
        return if (ApiClient::retrofit.isInitialized) {
            retrofit.create(ApiService::class.java)
        } else {
            create()
        }
    }

}
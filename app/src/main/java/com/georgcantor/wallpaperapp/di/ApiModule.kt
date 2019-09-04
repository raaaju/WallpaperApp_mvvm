package com.georgcantor.wallpaperapp.di

import android.app.Application
import com.georgcantor.wallpaperapp.BuildConfig
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.network.ApiService
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
open class ApiModule {

    private val baseUrl = "http://pixabay.com/api/"

    @Provides
    @Singleton
    open fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()

        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    open fun provideCache(application: Application): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong()
        val httpCacheDirectory = File(application.cacheDir, "http-cache")

        return Cache(httpCacheDirectory, cacheSize)
    }

    @Provides
    @Singleton
    open fun provideOkhttpClient(cache: Cache): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(ResponseCacheInterceptor())
        httpClient.addInterceptor(OfflineResponseCacheInterceptor())
        httpClient.cache(Cache(File(MyApplication.instance?.cacheDir, "ResponsesCache"),
                (10 * 1024 * 1024).toLong()))
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)

        return httpClient.build()
    }

    @Provides
    @Singleton
    open fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    open fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
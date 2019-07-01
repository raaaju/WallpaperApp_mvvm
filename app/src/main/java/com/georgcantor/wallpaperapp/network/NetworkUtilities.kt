package com.georgcantor.wallpaperapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import java.io.File
import java.util.concurrent.TimeUnit

class NetworkUtilities(private val context: Context) {

    val isInternetConnectionPresent: Boolean
        get() {
            val connectivityManager = this.context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo?
            networkInfo = connectivityManager.activeNetworkInfo

            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }

    fun getCall(type: String, index: Int): Call<Pic> {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(ResponseCacheInterceptor())
        httpClient.addInterceptor(OfflineResponseCacheInterceptor())
        httpClient.cache(Cache(File(MyApplication.getInstance()
                .cacheDir, "ResponsesCache"), (10 * 1024 * 1024).toLong()))
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)

        val client = ApiClient.getClient(httpClient).create(ApiService::class.java)
        val call: Call<Pic>
        call = client.getCatPic(type, index)

        return call
    }
}

package com.georgcantor.wallpaperapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkUtilities(private val context: Context) {

    val isInternetConnectionPresent: Boolean
        get() {
            val connectivityManager = this.context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo?
            networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
}

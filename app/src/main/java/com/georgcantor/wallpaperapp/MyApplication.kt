package com.georgcantor.wallpaperapp

import android.app.Application
import com.georgcantor.wallpaperapp.di.ApiComponent
import com.georgcantor.wallpaperapp.di.DaggerApiComponent

class MyApplication : Application() {

    private lateinit var apiComponent: ApiComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        apiComponent = DaggerApiComponent.builder()
                .application(this)
                .build()
    }

    companion object {
        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }

    fun getApiComponent(): ApiComponent = apiComponent
}

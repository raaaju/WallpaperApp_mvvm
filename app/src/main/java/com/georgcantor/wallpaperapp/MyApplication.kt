package com.georgcantor.wallpaperapp

import android.app.Application
import com.georgcantor.wallpaperapp.di.appModule
import com.georgcantor.wallpaperapp.di.repositoryModule
import com.georgcantor.wallpaperapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@MyApplication)
            modules(arrayListOf(appModule, viewModelModule, repositoryModule))
        }
    }

    companion object {
        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }

}

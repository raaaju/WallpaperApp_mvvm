package com.georgcantor.wallpaperapp

import android.app.Application
import com.georgcantor.wallpaperapp.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(apiModule, dbModule, repositoryModule, preferenceModule, viewModelModule))
        }
    }
}
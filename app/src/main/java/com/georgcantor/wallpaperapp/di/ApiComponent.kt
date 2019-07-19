package com.georgcantor.wallpaperapp.di

import android.app.Application
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.ui.MainActivity
import com.georgcantor.wallpaperapp.ui.fragment.BmwFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ApiModule::class])
interface ApiComponent : AndroidInjector<MyApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApiComponent
    }

    fun inject(mainActivity: MainActivity)

    fun inject(bmwFragment: BmwFragment)
}
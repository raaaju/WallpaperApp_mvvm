package com.georgcantor.wallpaperapp.di

import android.app.Application
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.ui.SearchActivity
import com.georgcantor.wallpaperapp.ui.fragment.BmwFragment
import com.georgcantor.wallpaperapp.ui.fragment.CarBrandFragment
import com.georgcantor.wallpaperapp.ui.fragment.MercedesFragment
import com.georgcantor.wallpaperapp.ui.fragment.SelectCatFragment
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

    fun inject(mercedesFragment: MercedesFragment)

    fun inject(bmwFragment: BmwFragment)

    fun inject(selectCatFragment: SelectCatFragment)

    fun inject(carBrandFragment: CarBrandFragment)

    fun inject(searchActivity: SearchActivity)
}
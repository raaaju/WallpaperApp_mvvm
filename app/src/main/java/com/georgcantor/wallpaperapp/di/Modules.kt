package com.georgcantor.wallpaperapp.di

import android.app.Activity
import com.georgcantor.wallpaperapp.model.local.FavDatabase
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.view.fragment.videos.VideosViewModel
import com.georgcantor.wallpaperapp.viewmodel.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ApiClient.create(get()) }
    single { FavDatabase.buildDefault(get()).dao() }
    single { PreferenceManager(androidApplication().applicationContext) }
}

val repositoryModule = module {
    single { ApiRepository(get()) }
}

val viewModelModule = module {
    viewModel {
        MainViewModel(androidApplication(), get(), get())
    }
    viewModel { (activity: Activity) ->
        FavoriteViewModel(activity, get())
    }
    viewModel {
        SearchViewModel(get())
    }
    viewModel { (activity: Activity) ->
        DetailsViewModel(androidApplication(), activity, get(), get())
    }
    viewModel {
        CategoryViewModel(androidApplication(), get())
    }
    viewModel {
        VideosViewModel(get())
    }
}

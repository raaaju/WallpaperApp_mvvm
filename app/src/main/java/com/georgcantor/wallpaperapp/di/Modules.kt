package com.georgcantor.wallpaperapp.di

import android.app.Activity
import com.georgcantor.wallpaperapp.model.local.FavDatabase
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.view.activity.MainActivity
import com.georgcantor.wallpaperapp.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { ApiRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { (manager: PreferenceManager, activity: MainActivity) ->
        MainViewModel(get(), get(), manager, activity)
    }
    viewModel { (activity: Activity) ->
        FavoriteViewModel(activity, get())
    }
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel { (activity: Activity) ->
        DetailsViewModel(get(), activity, get(), get())
    }
    viewModel { (manager: PreferenceManager) ->
        CategoryViewModel(get(), manager)
    }
}

val appModule = module {
    single { ApiClient.create(get()) }
    single { FavDatabase.buildDefault(get()).dao() }
}
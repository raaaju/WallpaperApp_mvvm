package com.georgcantor.wallpaperapp.di

import com.georgcantor.wallpaperapp.model.local.DatabaseHelper
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.viewmodel.CategoryViewModel
import com.georgcantor.wallpaperapp.viewmodel.DetailsViewModel
import com.georgcantor.wallpaperapp.viewmodel.FavoriteViewModel
import com.georgcantor.wallpaperapp.viewmodel.MainViewModel
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { ApiRepository(get(), get()) }
    single { DatabaseHelper(get()) }
}

val viewModelModule = module {
    viewModel {
        MainViewModel(get(), get())
    }
    viewModel {
        FavoriteViewModel(get(), get())
    }
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        DetailsViewModel(get(), get(), get())
    }
    viewModel { (manager: PreferenceManager) ->
        CategoryViewModel(get(), manager)
    }
}

val appModule = module {
    single { ApiClient.create(get()) }
}
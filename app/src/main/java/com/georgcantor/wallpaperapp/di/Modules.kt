package com.georgcantor.wallpaperapp.di

import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.viewmodel.CategoryViewModel
import com.georgcantor.wallpaperapp.viewmodel.DetailsViewModel
import com.georgcantor.wallpaperapp.viewmodel.FavoriteViewModel
import com.georgcantor.wallpaperapp.viewmodel.MainViewModel
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { ApiRepository(get()) }
    single { DatabaseHelper(get()) }
}

val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
    viewModel {
        FavoriteViewModel(get(), get())
    }
    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        DetailsViewModel(get(), get())
    }
    viewModel {
        CategoryViewModel(get(), get())
    }
}

val appModule = module {
    single { ApiClient.create(get()) }
}
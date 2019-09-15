package com.georgcantor.wallpaperapp.di

import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.viewmodel.FavoriteViewModel
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { ApiRepository(get()) }
}

val viewModelModule = module {
    viewModel {
        FavoriteViewModel(get())
    }
    viewModel {
        SearchViewModel(get())
    }
}

val appModule = module {
    single { ApiClient.create() }
}
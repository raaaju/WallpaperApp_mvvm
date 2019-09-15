package com.georgcantor.wallpaperapp.di

import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.viewmodel.BmwViewModel
import com.georgcantor.wallpaperapp.viewmodel.MercedesViewModel
import com.georgcantor.wallpaperapp.viewmodel.SelectCatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { ApiRepository(get()) }
}

val viewModelModule = module {
    viewModel {
        BmwViewModel(get())
    }
    viewModel {
        MercedesViewModel(get())
    }
    viewModel {
        SelectCatViewModel(get())
    }
}

val appModule = module {
    single { ApiClient.create() }
}
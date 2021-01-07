package com.georgcantor.wallpaperapp.di

import com.georgcantor.wallpaperapp.model.ApiClient
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.ui.activity.categories.CategoriesViewModel
import com.georgcantor.wallpaperapp.ui.fragment.GalleryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val apiModule = module {
    single { ApiClient.create(get()) }
}

val repositoryModule = module {
    single { Repository(get()) }
}

val viewModelModule = module(override = true) {
    viewModel { GalleryViewModel(get()) }
    viewModel { CategoriesViewModel(androidApplication(), get()) }
}
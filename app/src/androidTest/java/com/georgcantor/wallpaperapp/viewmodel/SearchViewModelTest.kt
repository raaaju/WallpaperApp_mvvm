package com.georgcantor.wallpaperapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.georgcantor.wallpaperapp.BaseAndroidTest
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class SearchViewModelTest : BaseAndroidTest() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchViewModel
    private lateinit var repository: ApiRepository

    @Before
    fun setUp() {
        repository = ApiRepository(getContext(), ApiClient.create(getContext()))
        viewModel = SearchViewModel(MyApplication(), repository)
    }

    @Test
    fun getPictures() {
        viewModel.getPics("test", 1).subscribe({
            Assert.assertNotNull(it)
        }, {
        })
    }

}
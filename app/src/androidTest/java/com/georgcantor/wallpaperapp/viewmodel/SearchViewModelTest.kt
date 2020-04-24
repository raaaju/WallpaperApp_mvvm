package com.georgcantor.wallpaperapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.georgcantor.wallpaperapp.BaseAndroidTest
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.ApiRepository
import junit.framework.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
            assertNotNull(it)
            assertTrue(it.size > 5)
        }, {
        })
    }

    @Test
    fun getPicsExceptPexelsUnsplash() {
        viewModel.getPicsExceptPexelsUnsplash("test", 1).subscribe({
            assertNotNull(it)
            assertTrue(it.size > 5)
        }, {
        })
    }

    @Test
    fun searchPics() {
        viewModel.searchPics("test", 1).subscribe({
            assertNotNull(it)
            assertTrue(it.size > 5)
        }, {
        })
    }

    @Test
    fun getAbyssRequest() {
        assertNotNull(viewModel.getAbyssRequest(1, Mark.BMW))
    }

}
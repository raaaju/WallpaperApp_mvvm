package com.georgcantor.wallpaperapp.ui.fragment

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.georgcantor.wallpaperapp.model.response.pixabay.Hit
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.repository.paging.PixabayPagingSource
import kotlinx.coroutines.flow.Flow

class GalleryViewModel(private val repository: Repository) : ViewModel() {

    fun getPicListStream(query: String): Flow<PagingData<Hit>> {
        return Pager(PagingConfig(20)) {
            PixabayPagingSource(repository, query)
        }.flow
    }
}
package com.georgcantor.wallpaperapp.ui.fragment

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.georgcantor.wallpaperapp.repository.PicPagingSource
import com.georgcantor.wallpaperapp.repository.Repository

class GalleryViewModel(private val repository: Repository) : ViewModel() {

    fun getPicListStream(query: String) = Pager(PagingConfig(40)) {
        PicPagingSource(repository, query)
    }.flow
}
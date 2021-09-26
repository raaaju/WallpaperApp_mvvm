package com.georgcantor.wallpaperapp.ui.fragment

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.georgcantor.wallpaperapp.repository.PicPagingSource
import com.georgcantor.wallpaperapp.repository.Repository

class GalleryViewModel(private val repository: Repository) : ViewModel() {

    var q: String = ""

    fun getPicListStream(query: String? = null) = Pager(PagingConfig(40)) {
        PicPagingSource(repository, query ?: q)
    }.flow
}
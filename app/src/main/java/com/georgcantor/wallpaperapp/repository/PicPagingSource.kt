package com.georgcantor.wallpaperapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import javax.inject.Inject

class PicPagingSource @Inject constructor(
    private val repository: Repository,
    private val query: String
) : PagingSource<Int, CommonPic>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommonPic> {
        return try {
            val nextPage = params.key ?: 1
            val response = repository.getPics(query, nextPage)
            LoadResult.Page(
                response,
                if (nextPage == 1) null else nextPage - 1,
                if (nextPage < response.size) nextPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CommonPic>): Int? {
        return null
    }
}
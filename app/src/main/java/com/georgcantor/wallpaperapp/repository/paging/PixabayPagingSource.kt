package com.georgcantor.wallpaperapp.repository.paging

import androidx.paging.PagingSource
import com.georgcantor.wallpaperapp.model.response.pixabay.Hit
import com.georgcantor.wallpaperapp.repository.Repository

class PixabayPagingSource(
    private val repository: Repository,
    private val query: String
) : PagingSource<Int, Hit>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Hit> {
        return try {
            val nextPage = params.key ?: 1
            val response = repository.getPixabayPictures(query, nextPage)
            LoadResult.Page(
                response.body()?.hits ?: emptyList(),
                if (nextPage == 1) null else nextPage - 1,
                if (nextPage < response.body()?.hits?.size ?: 0) nextPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
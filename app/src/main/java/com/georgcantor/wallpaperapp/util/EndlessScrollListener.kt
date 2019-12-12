package com.georgcantor.wallpaperapp.util

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessScrollListener(layoutManager: StaggeredGridLayoutManager)
    : RecyclerView.OnScrollListener() {

    private var visibleThreshold = 10
    private var currentPage = 1
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 1
    private var manager: RecyclerView.LayoutManager = layoutManager

    init {
        visibleThreshold *= layoutManager.spanCount
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = manager.itemCount
        val lastVisibleItemPositions = (manager as StaggeredGridLayoutManager)
                .findLastVisibleItemPositions(null)
        val lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)

        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, view)
            loading = true
        }
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView)

    fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }

        return maxSize
    }

}

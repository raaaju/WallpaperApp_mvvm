package com.georgcantor.wallpaperapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.INDEX
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_common.*
import org.koin.android.ext.android.inject

class MercedesFragment : Fragment(R.layout.fragment_common) {

    private val viewModel by inject<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!requireContext().isNetworkAvailable()) {
            noInternetAnimationView.showAnimation()
            context?.longToast(getString(R.string.no_internet))
        }

        val gridLayoutManager = StaggeredGridLayoutManager(
            requireContext().getScreenSize(),
            StaggeredGridLayoutManager.VERTICAL
        )
        val adapter = PicturesAdapter(getString(R.string.mercedes_request))
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter

        val scrollListener = object : EndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                PreferenceManager(requireContext()).saveInt(INDEX, page)
                viewModel.getPictures(getString(R.string.mercedes_request), page)
            }
        }
        scrollListener.resetState()
        recyclerView.addOnScrollListener(scrollListener)

        refreshLayout.setOnRefreshListener {
            viewModel.getPictures(getString(R.string.mercedes_request), 1)
            refreshLayout.isRefreshing = false
        }

        with(viewModel) {
            pictures.observe(viewLifecycleOwner, Observer(adapter::setPictures))

            isProgressVisible.observe(viewLifecycleOwner, Observer { visible ->
                if (visible) animationView.showAnimation() else animationView.hideAnimation()
            })

            getPictures(getString(R.string.mercedes_request), 1)
        }
    }
}

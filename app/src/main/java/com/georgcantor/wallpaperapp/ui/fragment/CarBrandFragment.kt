package com.georgcantor.wallpaperapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_common.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CarBrandFragment : Fragment() {

    companion object {
        const val FETCH_TYPE = "fetch_type"
    }

    private lateinit var viewModel: SearchViewModel
    lateinit var adapter: PicturesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireActivity().isNetworkAvailable()) {
            requireActivity().longToast(getString(R.string.check_internet))
        }
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_common, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!requireActivity().isNetworkAvailable()) {
            noInternetImageView.visible()
        }
        recyclerView.setHasFixedSize(true)

        val gridLayoutManager = StaggeredGridLayoutManager(
                requireContext().getScreenSize(),
                StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.layoutManager = gridLayoutManager
        adapter = PicturesAdapter(requireContext())
        recyclerView.adapter = adapter

        val listener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }

        refreshLayout.setOnRefreshListener {
            loadData(1)
            refreshLayout.isRefreshing = false
        }

        recyclerView.addOnScrollListener(listener)
        loadData(1)

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        recyclerView.addOnScrollListener(hideScrollListener)
    }

    private fun loadData(index: Int) {
        val disposable = viewModel.getPics(arguments?.getString(FETCH_TYPE) ?: "", index)
            .retry(3)
            .doOnSubscribe {
                animationView?.showAnimation()
            }
            .doFinally {
                animationView?.hideAnimation()
            }
            .subscribe(adapter::setPicList) {
                requireActivity().shortToast(getString(R.string.something_went_wrong))
            }

        DisposableManager.add(disposable)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}
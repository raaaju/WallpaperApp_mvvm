package com.georgcantor.wallpaperapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_common.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class BmwFragment : Fragment() {

    companion object {
        const val REQUEST = "request"
    }

    private lateinit var viewModel: SearchViewModel
    private var adapter: PicturesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val gridLayoutManager = StaggeredGridLayoutManager(
                requireContext().getScreenSize(),
                StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager
        adapter = PicturesAdapter(requireContext())
        recyclerView.adapter = adapter

        val scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        recyclerView.addOnScrollListener(scrollListener)

        refreshLayout.setOnRefreshListener {
            loadData(1)
            refreshLayout.isRefreshing = false
        }

        loadData(1)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

    private fun loadData(index: Int) {
        val disposable =
                viewModel.getAbyssPictures(viewModel.getAbyssRequest(index, Mark.BMW), index)
                        .doOnSubscribe {
                            animationView?.showAnimation()
                        }
                        .doFinally {
                            animationView?.hideAnimation()
                            try {
                                viewModel.noInternetShow.observe(viewLifecycleOwner, Observer {
                                    if (it) requireActivity().longToast(getString(R.string.no_internet))
                                })
                            } catch (e: IllegalStateException) {
                            }
                        }
                        .subscribe({
                            adapter?.setPictures(it)
                        }, {
                            viewModel.getPicsExceptPexelsUnsplash(getString(R.string.bmw_request), index)
                                    .subscribe({
                                        adapter?.setPictures(it)
                                    }, {
                                        viewModel.getPics(getString(R.string.bmw_request), index)
                                                .subscribe({
                                                    adapter?.setPictures(it)
                                                }, {
                                                })
                                    })
                        })

        DisposableManager.add(disposable)
    }

}

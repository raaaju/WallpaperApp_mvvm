package com.georgcantor.wallpaperapp.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.ui.fragment.BmwFragment.Companion.REQUEST
import com.georgcantor.wallpaperapp.ui.util.*
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_mercedes.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MercedesFragment : Fragment() {

    companion object {
        fun newInstance(arguments: String): MercedesFragment {
            val fragment = MercedesFragment()
            val args = Bundle()
            args.putString(REQUEST, arguments)
            fragment.arguments = args

            return fragment
        }
    }

    private lateinit var viewModel: SearchViewModel
    private var adapter: PicturesAdapter? = null

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
    ): View? = inflater.inflate(R.layout.fragment_mercedes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!requireActivity().isNetworkAvailable()) {
            noInternetImageView.visibility = View.VISIBLE
        }

        mercedesRefreshLayout.setOnRefreshListener {
            loadData(1)
            mercedesRefreshLayout.isRefreshing = false
        }

        val gridLayoutManager = StaggeredGridLayoutManager(
            UtilityMethods.getScreenSize(requireContext()),
            StaggeredGridLayoutManager.VERTICAL
        )
        mercedesRecyclerView.setHasFixedSize(true)
        mercedesRecyclerView.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        mercedesRecyclerView.addOnScrollListener(scrollListener)
        adapter = PicturesAdapter(requireContext())
        mercedesRecyclerView.adapter = adapter

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        mercedesRecyclerView.addOnScrollListener(hideScrollListener)

        loadData(1)
    }

    private fun loadData(index: Int) {
        animationView?.showAnimation()

        val disposable = viewModel.getPics(arguments?.getString(REQUEST) ?: "", index)
                .subscribe({
                    adapter?.setPicList(it)
                    animationView?.hideAnimation()
                }, {
                    animationView?.hideAnimation()
                    requireActivity().shortToast(getString(R.string.something_went_wrong))
                })

        DisposableManager.add(disposable)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}

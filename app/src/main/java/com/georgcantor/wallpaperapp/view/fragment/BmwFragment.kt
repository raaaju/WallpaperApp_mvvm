package com.georgcantor.wallpaperapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_common.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class BmwFragment : Fragment() {

    companion object {
        const val REQUEST = "request"

        fun newInstance(arguments: String): BmwFragment {
            val fragment = BmwFragment()
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

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        recyclerView.addOnScrollListener(hideScrollListener)

        loadData(1)
    }

    private fun loadData(index: Int) {
        val disposable = viewModel.getPics(arguments?.getString(REQUEST) ?: "", index)
            .retry(3)
            .doOnSubscribe {
                animationView?.showAnimation()
            }
            .doFinally {
                animationView?.hideAnimation()
            }
            .subscribe({
                adapter?.setPicList(it)
            }, {
                requireActivity().shortToast(it.message.toString())
            })

        DisposableManager.add(disposable)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}

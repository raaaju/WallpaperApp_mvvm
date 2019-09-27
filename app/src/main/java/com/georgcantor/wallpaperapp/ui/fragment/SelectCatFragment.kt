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
import com.georgcantor.wallpaperapp.ui.util.*
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_select_cat.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SelectCatFragment : Fragment() {

    companion object {
        const val EXTRA_CAT = "category"
    }

    private lateinit var viewModel: SearchViewModel
    lateinit var adapter: PicturesAdapter
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_select_cat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectCatRecyclerView.setHasFixedSize(true)
        type = arguments?.getString(EXTRA_CAT)

        if (!requireActivity().isNetworkAvailable()) {
            requireActivity().longToast(getString(R.string.check_internet))
        }
        val gridLayoutManager = StaggeredGridLayoutManager(
            UtilityMethods.getScreenSize(requireContext()),
            StaggeredGridLayoutManager.VERTICAL
        )
        selectCatRecyclerView.layoutManager = gridLayoutManager

        val listener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(type as String, page)
            }
        }
        selectCatRecyclerView.addOnScrollListener(listener)
        adapter = PicturesAdapter(requireContext())
        selectCatRecyclerView.adapter = adapter

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        selectCatRecyclerView.addOnScrollListener(hideScrollListener)

        loadData(type as String, 1)
    }

    private fun loadData(type: String, index: Int) {
        catAnimationView?.showAnimation()

        val disposable = viewModel.getPics(type, index)
                .subscribe({
                    adapter.setPicList(it)
                    catAnimationView?.hideAnimation()
                }, {
                    catAnimationView?.hideAnimation()
                    requireActivity().shortToast(getString(R.string.something_went_wrong))
                })

        DisposableManager.add(disposable)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}
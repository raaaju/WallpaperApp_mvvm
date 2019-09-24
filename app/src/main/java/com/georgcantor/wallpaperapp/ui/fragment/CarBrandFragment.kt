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
import com.georgcantor.wallpaperapp.ui.util.*
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_car_brand.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CarBrandFragment : Fragment() {

    companion object {
        const val FETCH_TYPE = "fetch_type"
    }

    private lateinit var viewModel: SearchViewModel
    lateinit var adapter: PicturesAdapter
    private var columnNo: Int = 0

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
    ): View? = inflater.inflate(R.layout.fragment_car_brand, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(1)
        brandRecyclerView.setHasFixedSize(true)
        checkScreenSize()

        val gridLayoutManager = StaggeredGridLayoutManager(columnNo, StaggeredGridLayoutManager.VERTICAL)
        brandRecyclerView.layoutManager = gridLayoutManager

        val listener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        brandRecyclerView.addOnScrollListener(listener)
        adapter = PicturesAdapter(requireContext())
        brandRecyclerView.adapter = adapter

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        brandRecyclerView.addOnScrollListener(hideScrollListener)
    }

    private fun loadData(index: Int) {
        brandAnimationView?.showAnimation()

        val disposable = viewModel.getPics(arguments?.getString(FETCH_TYPE) ?: "", index)
                .subscribe({
                    adapter.setPicList(it)
                    brandAnimationView?.hideAnimation()
                }, {
                    brandAnimationView?.hideAnimation()
                    requireActivity().shortToast(getString(R.string.something_went_wrong))
                })

        DisposableManager.add(disposable)
    }

    private fun checkScreenSize() {
        val screenSize = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        columnNo = when (screenSize) {
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> 4
            Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> 3
            Configuration.SCREENLAYOUT_SIZE_LARGE -> 3
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> 2
            Configuration.SCREENLAYOUT_SIZE_SMALL -> 2
            else -> 2
        }
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}
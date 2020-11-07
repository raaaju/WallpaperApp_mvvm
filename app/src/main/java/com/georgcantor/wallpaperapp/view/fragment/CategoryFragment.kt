package com.georgcantor.wallpaperapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.REQUEST
import com.georgcantor.wallpaperapp.view.activity.CarBrandActivity
import com.georgcantor.wallpaperapp.view.adapter.CategoryAdapter
import com.georgcantor.wallpaperapp.viewmodel.CategoryViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_common.*
import org.koin.android.ext.android.inject

class CategoryFragment : Fragment(R.layout.fragment_common) {

    private val viewModel by inject<CategoryViewModel>()
    private val disposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!requireContext().isNetworkAvailable()) context?.longToast(getString(R.string.no_internet))

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), requireContext().getScreenSize())

        refreshLayout.setOnRefreshListener {
            loadData()
            refreshLayout.isRefreshing = false
        }

        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun loadData() {
        disposable.add(
            viewModel.getSavedCategories()
                .doOnSubscribe { animationView?.showAnimation() }
                .doFinally { animationView?.hideAnimation() }
                .subscribe({
                    recyclerView.adapter =
                        CategoryAdapter(it as MutableList<Category>) { category ->
                            requireActivity().openActivity(CarBrandActivity::class.java) {
                                putString(REQUEST, category.categoryName)
                            }
                        }
                }, {
                })
        )
    }
}

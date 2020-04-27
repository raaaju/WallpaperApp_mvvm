package com.georgcantor.wallpaperapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CategoryFragment : Fragment() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var preferenceManager: PreferenceManager
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(requireActivity())
        viewModel = getViewModel { parametersOf(preferenceManager) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_common, container, false)

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
                        CategoryAdapter(requireContext(), it as MutableList<Category>) { category ->
                            requireActivity().openActivity(CarBrandActivity::class.java) {
                                putString(REQUEST, category.categoryName)
                            }
                        }
                }, {
                })
        )
    }
}

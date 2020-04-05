package com.georgcantor.wallpaperapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.Companion.REQUEST
import com.georgcantor.wallpaperapp.view.activity.CarBrandActivity
import com.georgcantor.wallpaperapp.view.adapter.CategoryAdapter
import com.georgcantor.wallpaperapp.viewmodel.CategoryViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_common.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CategoryFragment : Fragment() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var preferenceManager: PreferenceManager

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
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(activity, requireContext().getScreenSize())

        refreshLayout.setOnRefreshListener {
            loadData()
            refreshLayout.isRefreshing = false
        }

        loadData()
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

    private fun loadData() {
        val disposable: Disposable = viewModel.getSavedCategories()
            .doOnSubscribe {
                animationView?.showAnimation()
            }
            .doFinally {
                animationView?.hideAnimation()
                try {
                    viewModel.noInternetShow.observe(viewLifecycleOwner, Observer {
                        if (it) requireActivity().shortToast(getString(R.string.no_internet))
                    })
                } catch (e: IllegalStateException) {
                }
            }
            .subscribe({
                recyclerView.adapter =
                    CategoryAdapter(requireContext(), it as MutableList<Category>) { category ->
                        requireActivity().openActivity(CarBrandActivity::class.java) {
                            putString(REQUEST, category.categoryName)
                        }
                    }
            }, {
            })
        DisposableManager.add(disposable)
    }
}

package com.georgcantor.wallpaperapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.adapter.CategoryAdapter
import com.georgcantor.wallpaperapp.util.DisposableManager
import com.georgcantor.wallpaperapp.util.HideNavScrollListener
import com.georgcantor.wallpaperapp.util.UtilityMethods
import com.georgcantor.wallpaperapp.util.hideAnimation
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.util.showAnimation
import com.georgcantor.wallpaperapp.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CategoryFragment : Fragment() {

    companion object {
        fun newInstance(): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }

    private lateinit var viewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!requireActivity().isNetworkAvailable()) {
            noInternetImageView.visibility = View.VISIBLE
        }
        categoryRecyclerView.setHasFixedSize(true)
        categoryRecyclerView.layoutManager =
            GridLayoutManager(activity, UtilityMethods.getScreenSize(requireContext()))

        val categoryAdapter = CategoryAdapter(requireContext(), requireFragmentManager())
        categoryRecyclerView.adapter = categoryAdapter

        val disposable = viewModel.getCategories()
            .retry(3)
            .doOnSubscribe {
                animationView?.showAnimation()
            }
            .doOnTerminate {
                animationView?.hideAnimation()
            }
            .subscribe(categoryAdapter::setCategoryList) {
                requireActivity().shortToast(getString(R.string.something_went_wrong))
            }

        DisposableManager.add(disposable)

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        categoryRecyclerView.addOnScrollListener(hideScrollListener)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}

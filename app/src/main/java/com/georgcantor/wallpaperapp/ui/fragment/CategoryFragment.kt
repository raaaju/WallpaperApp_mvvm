package com.georgcantor.wallpaperapp.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Category
import com.georgcantor.wallpaperapp.ui.adapter.CategoryAdapter
import com.georgcantor.wallpaperapp.ui.util.HideNavScrollListener
import kotlinx.android.synthetic.main.app_bar_main.navigation
import kotlinx.android.synthetic.main.fragment_category.*
import java.util.*

class CategoryFragment : Fragment() {

    companion object {
        fun newInstance(): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }

    private val categoryList = ArrayList<Category>()
    private var columnNo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryRecyclerView.setHasFixedSize(true)
        checkScreenSize()
        categoryRecyclerView.layoutManager = GridLayoutManager(activity, columnNo)
        val categoryAdapter = CategoryAdapter(requireContext(), requireFragmentManager())

        categoryAdapter.setCategoryList(categoryList)
        categoryRecyclerView.adapter = categoryAdapter

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        categoryRecyclerView.addOnScrollListener(hideScrollListener)
    }

    private fun addToList() {
        categoryList.add(Category(resources.getString(R.string.Animals), resources.getString(R.string.animals)))
        categoryList.add(Category(resources.getString(R.string.Textures), resources.getString(R.string.textures)))
        categoryList.add(Category(resources.getString(R.string.Architecture), resources.getString(R.string.buildings)))
        categoryList.add(Category(resources.getString(R.string.Business), resources.getString(R.string.business)))
        categoryList.add(Category(resources.getString(R.string.Communication), resources.getString(R.string.computer)))
        categoryList.add(Category(resources.getString(R.string.Education), resources.getString(R.string.education)))
        categoryList.add(Category(resources.getString(R.string.Fashion), resources.getString(R.string.fashion)))
        categoryList.add(Category(resources.getString(R.string.Emotions), resources.getString(R.string.feelings)))
        categoryList.add(Category(resources.getString(R.string.Food), resources.getString(R.string.food)))
        categoryList.add(Category(resources.getString(R.string.Health), resources.getString(R.string.health)))
        categoryList.add(Category(resources.getString(R.string.Craft), resources.getString(R.string.industry)))
        categoryList.add(Category(resources.getString(R.string.Music), resources.getString(R.string.music)))
        categoryList.add(Category(resources.getString(R.string.Nature), resources.getString(R.string.nature)))
        categoryList.add(Category(resources.getString(R.string.People), resources.getString(R.string.people)))
        categoryList.add(Category(resources.getString(R.string.Places), resources.getString(R.string.places)))
        categoryList.add(Category(resources.getString(R.string.Religion), resources.getString(R.string.religion)))
        categoryList.add(Category(resources.getString(R.string.Technology), resources.getString(R.string.science)))
        categoryList.add(Category(resources.getString(R.string.Sports), resources.getString(R.string.sports)))
        categoryList.add(Category(resources.getString(R.string.Transportation), resources.getString(R.string.transportation)))
        categoryList.add(Category(resources.getString(R.string.Travel), resources.getString(R.string.travel)))
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
}

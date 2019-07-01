package com.georgcantor.wallpaperapp.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Category
import com.georgcantor.wallpaperapp.ui.adapter.CategoryAdapter
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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)
        recyclerView.setHasFixedSize(true)
        checkScreenSize()
        recyclerView.layoutManager = GridLayoutManager(activity, columnNo)
        val categoryAdapter = CategoryAdapter(requireContext(), requireActivity().supportFragmentManager)

        populate()
        categoryAdapter.setCategoryList(categoryList)
        recyclerView.adapter = categoryAdapter

        return view
    }

    private fun populate() {
        categoryList.add(Category(resources.getString(R.string.Animals), resources.getString(R.string.animals)))
        categoryList.add(Category(resources.getString(R.string.Textures), resources.getString(R.string.backgrounds)))
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

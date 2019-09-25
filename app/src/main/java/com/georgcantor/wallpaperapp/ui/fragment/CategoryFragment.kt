package com.georgcantor.wallpaperapp.ui.fragment

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
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
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
        categoryRecyclerView.layoutManager =
            GridLayoutManager(activity, UtilityMethods.getScreenSize(requireContext()))

        val categoryAdapter = CategoryAdapter(requireContext(), requireFragmentManager())
        categoryAdapter.setCategoryList(categoryList)
        categoryRecyclerView.adapter = categoryAdapter

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        categoryRecyclerView.addOnScrollListener(hideScrollListener)
    }

    private fun addToList() {
        categoryList.add(Category(getString(R.string.Animals), getString(R.string.animals)))
        categoryList.add(Category(getString(R.string.Textures), getString(R.string.textures)))
        categoryList.add(Category(getString(R.string.Architecture), getString(R.string.buildings)))
        categoryList.add(Category(getString(R.string.Business), getString(R.string.business)))
        categoryList.add(Category(getString(R.string.Communication), getString(R.string.computer)))
        categoryList.add(Category(getString(R.string.Education), getString(R.string.education)))
        categoryList.add(Category(getString(R.string.Fashion), getString(R.string.fashion)))
        categoryList.add(Category(getString(R.string.Emotions), getString(R.string.feelings)))
        categoryList.add(Category(getString(R.string.Food), getString(R.string.food)))
        categoryList.add(Category(getString(R.string.Health), getString(R.string.health)))
        categoryList.add(Category(getString(R.string.Craft), getString(R.string.industry)))
        categoryList.add(Category(getString(R.string.Music), getString(R.string.music)))
        categoryList.add(Category(getString(R.string.Nature), getString(R.string.nature)))
        categoryList.add(Category(getString(R.string.People), getString(R.string.people)))
        categoryList.add(Category(getString(R.string.Places), getString(R.string.places)))
        categoryList.add(Category(getString(R.string.Religion), getString(R.string.religion)))
        categoryList.add(Category(getString(R.string.Technology), getString(R.string.science)))
        categoryList.add(Category(getString(R.string.Sports), getString(R.string.sports)))
        categoryList.add(Category(getString(R.string.Transportation), getString(R.string.transportation)))
        categoryList.add(Category(getString(R.string.Travel), getString(R.string.travel)))
    }

}

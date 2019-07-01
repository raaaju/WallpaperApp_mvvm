package com.georgcantor.wallpaperapp.ui.adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Category
import com.georgcantor.wallpaperapp.ui.fragment.SelectCatFragment
import com.georgcantor.wallpaperapp.ui.util.ExploreViewHolder
import java.util.*

class CategoryAdapter(private val context: Context,
                      private val fragmentManager: FragmentManager) : RecyclerView.Adapter<ExploreViewHolder>() {

    private val categoryList: MutableList<Category>?

    init {
        this.categoryList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_explore, null)
        val viewHolder = ExploreViewHolder(itemView)

        itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val bundle = Bundle()
            bundle.putString(SelectCatFragment.EXTRA_CAT, categoryList?.get(position)?.categoryDrawId)
            val fragment = SelectCatFragment()
            fragment.arguments = bundle

            openFragment(fragment)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        val category = categoryList!![position]
        val id = context.resources.getIdentifier(context.resources
                .getString(R.string.package_drawable) + category.categoryDrawId, null, null)
        holder.categoryName.text = category.categoryName
        holder.category.setImageResource(id)
    }

    override fun getItemCount(): Int {
        return categoryList?.size ?: 0
    }

    fun setCategoryList(categories: List<Category>?) {
        if (categories != null) {
            this.categoryList!!.clear()
            this.categoryList.addAll(categories)
            notifyDataSetChanged()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}

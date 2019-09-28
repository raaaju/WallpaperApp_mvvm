package com.georgcantor.wallpaperapp.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Category
import com.georgcantor.wallpaperapp.ui.adapter.holder.CategoryViewHolder
import com.georgcantor.wallpaperapp.ui.fragment.CarBrandFragment
import java.util.*

class CategoryAdapter(private val context: Context,
                      private val fragmentManager: FragmentManager) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categoryList: MutableList<Category>?

    init {
        this.categoryList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.category_item, null)
        val viewHolder = CategoryViewHolder(itemView)

        itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val bundle = Bundle()
            bundle.putString(CarBrandFragment.FETCH_TYPE, categoryList?.get(position)?.categoryDrawId)
            val fragment = CarBrandFragment()
            fragment.arguments = bundle

            openFragment(fragment)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList?.get(position)
        val id = context.resources.getIdentifier(context.resources
                .getString(R.string.package_drawable) + category?.categoryDrawId, null, null)
        holder.categoryName.text = category?.categoryName
        holder.categoryImage.setImageResource(id)
    }

    override fun getItemCount(): Int = categoryList?.size ?: 0

    fun setCategoryList(categories: List<Category>?) {
        if (categories != null) {
            this.categoryList?.clear()
            this.categoryList?.addAll(categories)
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

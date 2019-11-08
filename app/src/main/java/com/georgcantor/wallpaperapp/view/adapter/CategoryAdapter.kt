package com.georgcantor.wallpaperapp.view.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.view.adapter.holder.CategoryViewHolder
import com.georgcantor.wallpaperapp.view.fragment.CarBrandFragment
import com.georgcantor.wallpaperapp.util.openFragment
import kotlin.collections.ArrayList

class CategoryAdapter(private val context: Context) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categories: MutableList<Category>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.category_item, null)
        val viewHolder = CategoryViewHolder(itemView)
        val activity = context as AppCompatActivity

        itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val bundle = Bundle()
            bundle.putString(CarBrandFragment.FETCH_TYPE, categories?.get(position)?.categoryName)
            val fragment = CarBrandFragment()
            fragment.arguments = bundle

            activity.openFragment(fragment, categories?.get(position)?.categoryName ?: "", false)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryName.text = categories?.get(position)?.categoryName

        Glide.with(context)
            .load(categories?.get(position)?.categoryUrl)
            .thumbnail(0.1F)
            .placeholder(R.drawable.plh)
            .into(holder.categoryImage)
    }

    override fun getItemCount(): Int = categories?.size ?: 0

    fun setCategoryList(categories: List<Category>?) {
        if (categories != null) {
            this.categories?.clear()
            this.categories?.addAll(categories)
            notifyDataSetChanged()
        }
    }

}

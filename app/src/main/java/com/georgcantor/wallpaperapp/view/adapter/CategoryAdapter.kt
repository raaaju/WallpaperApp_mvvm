package com.georgcantor.wallpaperapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.view.adapter.holder.CategoryViewHolder

class CategoryAdapter(
    categories: MutableList<Category>,
    private val clickListener: (Category) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categories = mutableListOf<Category>()

    init {
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_category, null)
    )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        with(holder) {
            categoryName.text = category.categoryName
            itemView.context.loadImage(category.categoryUrl, categoryImage, null)
            itemView.setOnClickListener { clickListener(category) }
        }
    }

    override fun getItemCount(): Int = categories.size
}

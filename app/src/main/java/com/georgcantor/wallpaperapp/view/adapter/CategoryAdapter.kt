package com.georgcantor.wallpaperapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.view.adapter.holder.CategoryViewHolder

class CategoryAdapter(
    private val context: Context,
    categories: MutableList<Category>,
    private val clickListener: (Category) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categories = mutableListOf<Category>()

    init {
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_category, null)
        val viewHolder = CategoryViewHolder(itemView)

        itemView.setOnClickListener {
            clickListener(categories[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.categoryName

        context.loadImage(
            category.categoryUrl,
            holder.categoryImage,
            null
        )
    }

    override fun getItemCount(): Int = categories.size
}

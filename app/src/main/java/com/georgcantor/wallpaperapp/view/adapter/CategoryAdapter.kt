package com.georgcantor.wallpaperapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.openActivity
import com.georgcantor.wallpaperapp.view.adapter.holder.CategoryViewHolder
import com.georgcantor.wallpaperapp.view.activity.CarBrandActivity
import com.georgcantor.wallpaperapp.view.fragment.BmwFragment.Companion.REQUEST
import kotlin.collections.ArrayList

class CategoryAdapter(private val context: Context) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categories: MutableList<Category>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.category_item, null)
        val viewHolder = CategoryViewHolder(itemView)

        itemView.setOnClickListener {
            context.openActivity(CarBrandActivity::class.java) {
                putString(REQUEST, categories?.get(viewHolder.adapterPosition)?.categoryName ?: "")
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryName.text = categories?.get(position)?.categoryName

        context.loadImage(
            categories?.get(position)?.categoryUrl ?: "",
            context.resources.getDrawable(R.drawable.placeholder),
            holder.categoryImage,
            null
        )
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

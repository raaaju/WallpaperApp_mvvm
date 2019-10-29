package com.georgcantor.wallpaperapp.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.adapter.holder.CategoryViewHolder
import com.georgcantor.wallpaperapp.ui.fragment.CarBrandFragment
import com.georgcantor.wallpaperapp.util.openFragment
import kotlin.collections.ArrayList

class CategoryAdapter(private val context: Context) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categoryList: MutableList<String>? = ArrayList()
    private val names: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.category_item, null)
        val viewHolder = CategoryViewHolder(itemView)
        val activity = context as AppCompatActivity

        itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val bundle = Bundle()
            bundle.putString(CarBrandFragment.FETCH_TYPE, names[position])
            val fragment = CarBrandFragment()
            fragment.arguments = bundle

            activity.openFragment(fragment, categoryList?.get(position) ?: "", false)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList?.get(position)
        holder.categoryName.text = names[position]

        Glide.with(context)
            .load(category)
            .thumbnail(0.1f)
            .placeholder(R.drawable.plh)
            .into(holder.categoryImage)
    }

    override fun getItemCount(): Int = categoryList?.size ?: 0

    fun setCategoryList(categories: List<String>?) {
        if (categories != null) {
            this.categoryList?.clear()
            this.categoryList?.addAll(categories)
            notifyDataSetChanged()
        }
    }

    fun setCategoryNames(names: ArrayList<String>) {
        this.names.addAll(names)
    }

}

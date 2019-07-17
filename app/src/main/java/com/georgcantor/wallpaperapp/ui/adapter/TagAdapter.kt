package com.georgcantor.wallpaperapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.util.TagViewHolder
import java.util.*

class TagAdapter(private val context: Context) : RecyclerView.Adapter<TagViewHolder>() {

    private val tags: MutableList<String>?

    init {
        this.tags = ArrayList()
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.tag_item, null)
        return TagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        this.tags?.let {
            val tag = it[position]
            holder.tag.text = tag
        }
    }

    override fun getItemCount(): Int {
        return tags?.size ?: 0
    }

    fun setTagList(tags: List<String>) {
        this.tags?.clear()
        this.tags?.addAll(tags)
        notifyDataSetChanged()
    }
}

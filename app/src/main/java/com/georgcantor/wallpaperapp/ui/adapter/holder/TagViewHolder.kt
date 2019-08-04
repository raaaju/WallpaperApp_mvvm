package com.georgcantor.wallpaperapp.ui.adapter.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tag_item.view.*

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var tag: TextView = itemView.tag_item
}

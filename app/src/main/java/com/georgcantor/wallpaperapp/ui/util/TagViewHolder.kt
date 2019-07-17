package com.georgcantor.wallpaperapp.ui.util

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.tag_item.view.*

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var tag: TextView = itemView.tag_item
}

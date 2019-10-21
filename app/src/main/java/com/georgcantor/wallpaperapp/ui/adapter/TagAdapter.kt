package com.georgcantor.wallpaperapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.DetailsActivity
import com.georgcantor.wallpaperapp.ui.MainActivity
import com.georgcantor.wallpaperapp.ui.adapter.holder.TagViewHolder
import java.util.*

class TagAdapter(private val context: Context) : RecyclerView.Adapter<TagViewHolder>() {

    private val tags: MutableList<String>?
    private lateinit var activity: DetailsActivity

    init {
        this.tags = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.tag_item, null)
        return TagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        this.tags?.let {
            val tag = it[position]
            holder.tag.text = tag

            holder.tag.setOnClickListener {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra(MainActivity.TAG_EXTRA_OPEN, MainActivity.TAG_EXTRA_OPEN)
                intent.putExtra(MainActivity.TAG_EXTRA, tag)
                startActivity(context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int = tags?.size ?: 0

    fun setTagList(tags: List<String>, activity: DetailsActivity) {
        this.activity = activity
        this.tags?.clear()
        this.tags?.addAll(tags)
        notifyDataSetChanged()
    }

}

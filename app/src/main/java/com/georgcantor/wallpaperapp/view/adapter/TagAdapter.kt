package com.georgcantor.wallpaperapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.applySchedulers
import com.georgcantor.wallpaperapp.view.adapter.holder.TagViewHolder
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class TagAdapter(
    private val context: Context,
    tags: MutableList<String>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<TagViewHolder>() {

    private val tags: MutableList<String>? = ArrayList()

    init {
        this.tags?.clear()
        this.tags?.addAll(tags)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.tag_item, null)
        return TagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        this.tags?.let {
            val tag = it[position]
            holder.tag.text = tag

            val publishSubject = PublishSubject.create<Int>()
            publishSubject
                .throttleFirst(1, TimeUnit.SECONDS)
                .applySchedulers()
                .subscribe {
                    clickListener(tag)
                }

            holder.tag.setOnClickListener {
                publishSubject.onNext(0)
            }
        }
    }

    override fun getItemCount(): Int = tags?.size ?: 0
}

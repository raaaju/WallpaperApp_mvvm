package com.georgcantor.wallpaperapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.ui.PicDetailActivity
import com.georgcantor.wallpaperapp.ui.adapter.holder.WallpViewHolder
import com.georgcantor.wallpaperapp.ui.util.longToast
import com.squareup.picasso.Picasso
import java.util.*

class PicturesAdapter(private val context: Context) : RecyclerView.Adapter<WallpViewHolder>() {

    private val commonPics: MutableList<CommonPic>?

    init {
        this.commonPics = ArrayList()
    }

    fun setPicList(strings: MutableList<CommonPic>) {
        this.commonPics?.addAll(strings)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.wallp_item, null)
        val wallpViewHolder = WallpViewHolder(itemView)

        itemView.setOnClickListener {
            val activity = context as Activity
            val position = wallpViewHolder.adapterPosition
            val intent = Intent(context, PicDetailActivity::class.java)
            try {
                intent.putExtra(PicDetailActivity.EXTRA_PIC, commonPics?.get(position)?.heght?.let { height ->
                    CommonPic(
                            url = commonPics[position].url,
                            width = commonPics[position].width,
                            heght = height,
                            likes = commonPics[position].likes,
                            favorites = commonPics[position].favorites,
                            tags = commonPics[position].tags,
                            downloads = commonPics[position].downloads,
                            imageURL = commonPics[position].imageURL,
                            fullHDURL = commonPics[position].fullHDURL,
                            user = commonPics[position].user,
                            userImageURL = commonPics[position].userImageURL
                    )
                })
            } catch (e: ArrayIndexOutOfBoundsException) {
                context.longToast(context.resources.getString(R.string.something_went_wrong))
            }
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        return wallpViewHolder
    }

    override fun onBindViewHolder(holder: WallpViewHolder, position: Int) {
        this.commonPics.let {
            val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
            val height = it?.get(position)?.heght?.toFloat()
            val width = it?.get(position)?.width?.toFloat()
            val ratio = height?.div(width!!)
            layoutParams.height = (layoutParams.width * ratio!!).toInt()
            holder.imageView.layoutParams = layoutParams
            holder.imageView.setRatio(ratio)

            Picasso.with(context)
                .load(it[position].url)
                .placeholder(R.drawable.plh)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = commonPics?.size ?: 0

}
package com.georgcantor.wallpaperapp.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.Constants.EXTRA_PIC
import com.georgcantor.wallpaperapp.util.applySchedulers
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.openActivity
import com.georgcantor.wallpaperapp.view.activity.DetailActivity
import com.georgcantor.wallpaperapp.view.adapter.holder.PictureViewHolder
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PicturesAdapter(private val tag: String) : RecyclerView.Adapter<PictureViewHolder>() {

    private val commonPics = mutableListOf<CommonPic>()

    fun setPictures(pictures: MutableList<CommonPic>) {
        this.commonPics.addAll(pictures)
        notifyDataSetChanged()
    }

    fun clearPictures() {
        val size = commonPics.size
        commonPics.clear()
        notifyItemRangeRemoved(0, size)
    }

    @SuppressLint("CheckResult")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_picture, null)
        val viewHolder = PictureViewHolder(itemView)

        val publishSubject = PublishSubject.create<Int>()
        publishSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .applySchedulers()
            .subscribe {
                with(commonPics[viewHolder.adapterPosition]) {
                    parent.context.openActivity(DetailActivity::class.java) {
                        putParcelable(
                            EXTRA_PIC,
                            CommonPic(
                                url = url,
                                width = width,
                                heght = heght,
                                tags = tag,
                                imageURL = imageURL,
                                fullHDURL = fullHDURL,
                                id = id,
                                videoId = videoId
                            )
                        )
                    }
                }
            }

        itemView.setOnClickListener {
            publishSubject.onNext(0)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val pic = commonPics[position]
        val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
        val height = pic.heght.toFloat()
        val width = pic.width.toFloat()
        val ratio = width.let(height::div)
        layoutParams.height = (layoutParams.width * ratio).toInt()
        holder.imageView.layoutParams = layoutParams
        holder.imageView.setRatio(ratio)

        holder.itemView.context.loadImage(
            pic.url ?: "",
            holder.imageView,
            null
        )
    }

    override fun getItemCount(): Int = commonPics.size
}
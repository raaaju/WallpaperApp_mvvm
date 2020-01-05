package com.georgcantor.wallpaperapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.Constants.Companion.EXTRA_PIC
import com.georgcantor.wallpaperapp.util.applySchedulers
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.openActivity
import com.georgcantor.wallpaperapp.view.activity.DetailsActivity
import com.georgcantor.wallpaperapp.view.adapter.holder.PictureViewHolder
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class PicturesAdapter(private val context: Context) : RecyclerView.Adapter<PictureViewHolder>() {

    private val commonPics: MutableList<CommonPic>?

    init {
        this.commonPics = ArrayList()
    }

    fun setPictures(pictures: MutableList<CommonPic>) {
        this.commonPics?.addAll(pictures)
        notifyDataSetChanged()
    }

    fun clearPictures() {
        val size = commonPics?.size
        commonPics?.clear()
        size?.let { notifyItemRangeRemoved(0, it) }
    }

    @SuppressLint("CheckResult")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.picture_item, null)
        val viewHolder = PictureViewHolder(itemView)

        val publishSubject = PublishSubject.create<Int>()
        publishSubject
                .throttleFirst(1, TimeUnit.SECONDS)
                .applySchedulers()
                .subscribe {
                    val position = viewHolder.adapterPosition

                    context.openActivity(DetailsActivity::class.java) {
                        putParcelable(
                                EXTRA_PIC,
                                commonPics?.get(position)?.url?.let { url ->
                                    CommonPic(
                                            url = url,
                                            width = commonPics[position].width,
                                            heght = commonPics[position].heght,
                                            favorites = commonPics[position].favorites,
                                            tags = commonPics[position].tags,
                                            downloads = commonPics[position].downloads,
                                            imageURL = commonPics[position].imageURL,
                                            fullHDURL = commonPics[position].fullHDURL,
                                            user = commonPics[position].user,
                                            userImageURL = commonPics[position].userImageURL
                                    )
                                }
                        )
                    }
                }

        itemView.setOnClickListener {
            publishSubject.onNext(0)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        this.commonPics.let {
            val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
            val height = it?.get(position)?.heght?.toFloat()
            val width = it?.get(position)?.width?.toFloat()
            val ratio = width?.let { widthFloat -> height?.div(widthFloat) } ?: 0F
            layoutParams.height = (layoutParams.width * ratio).toInt()
            holder.imageView.layoutParams = layoutParams
            holder.imageView.setRatio(ratio)

            context.loadImage(
                    it?.get(position)?.url ?: "",
                    context.resources.getDrawable(R.drawable.placeholder),
                    holder.imageView,
                    null
            )
        }
    }

    override fun getItemCount(): Int = commonPics?.size ?: 0

}
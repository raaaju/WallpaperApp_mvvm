package com.georgcantor.wallpaperapp.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.FavDao
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.openActivity
import com.georgcantor.wallpaperapp.util.showDialog
import com.georgcantor.wallpaperapp.view.activity.DetailsActivity
import com.georgcantor.wallpaperapp.view.activity.DetailsActivity.Companion.EXTRA_PIC
import com.georgcantor.wallpaperapp.view.adapter.holder.FavoriteViewHolder
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class FavoriteAdapter(
    private val context: Context,
    private val dao: FavDao
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val favorites: MutableList<Favorite>?
    private val activity = context as Activity

    init {
        this.favorites = ArrayList()
    }

    fun setFavorites(strings: MutableList<Favorite>) {
        clearPictures()
        this.favorites?.addAll(strings)
        notifyDataSetChanged()
    }

    private fun clearPictures() {
        val size = favorites?.size
        favorites?.clear()
        size?.let { notifyItemRangeRemoved(0, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder =
            FavoriteViewHolder(LayoutInflater.from(context).inflate(R.layout.favorite_item, null))

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favorites?.get(position)
        val hitJson = favorite?.hit
        val pic = Gson().fromJson(hitJson, CommonPic::class.java)

        val publishSubject = PublishSubject.create<Int>()
        publishSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                context.openActivity(DetailsActivity::class.java) {
                    putParcelable(
                        EXTRA_PIC,
                        CommonPic(
                            url = pic.url,
                            width = pic.width,
                            heght = pic.heght,
                            likes = pic.likes,
                            favorites = pic.favorites,
                            tags = pic.tags,
                            downloads = pic.downloads,
                            imageURL = pic.imageURL,
                            fullHDURL = pic.fullHDURL,
                            user = pic.user,
                            userImageURL = pic.userImageURL
                        )
                    )
                }
            }

        holder.imageView.setOnClickListener {
            publishSubject.onNext(0)
        }

        holder.imageView.setOnLongClickListener {
            val fav = favorites?.get(position)

            context.showDialog(context.getString(R.string.del_from_fav_dialog)) {
                Observable.fromCallable {
                    fav?.url?.let(dao::deleteByUrl)
                    activity.runOnUiThread(activity::recreate)
                }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
            }
            false
        }

        val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
        val height = pic.heght.toFloat()
        val width = pic.width.toFloat()
        val ratio = height.div(width)
        layoutParams.height = (layoutParams.width * ratio).toInt()
        holder.imageView.layoutParams = layoutParams
        holder.imageView.setRatio(ratio)

        context.loadImage(
            pic.url ?: "",
            context.resources.getDrawable(R.drawable.placeholder),
            holder.imageView,
            null
        )
    }

    override fun getItemCount(): Int = favorites?.size ?: 0

}
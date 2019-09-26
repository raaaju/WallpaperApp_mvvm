package com.georgcantor.wallpaperapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.local.db.Favorite
import com.georgcantor.wallpaperapp.ui.FavoriteActivity
import com.georgcantor.wallpaperapp.ui.PicDetailActivity
import com.georgcantor.wallpaperapp.ui.util.longToast
import com.georgcantor.wallpaperapp.ui.util.showDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.*

class FavoriteAdapter(
    private val context: Context,
    private val layout: Int,
    private val favoriteArrayList: ArrayList<Favorite>
) : BaseAdapter() {

    private lateinit var db: DatabaseHelper

    override fun getCount(): Int = favoriteArrayList.size

    override fun getItem(position: Int): Any = favoriteArrayList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private inner class ViewHolder {
        internal lateinit var imageView: ImageView
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View? {
        var row: View? = view
        var holder = ViewHolder()

        if (row == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(layout, null)

            if (row != null) {
//                holder.imageView = row.imgFavorite
                row.tag = holder
            }
        } else {
            holder = row.tag as ViewHolder
        }

        val favorite = favoriteArrayList[position]

        Picasso.with(context)
            .load(favorite.imageUrl)
            .placeholder(R.drawable.plh)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            val activity = context as Activity
            val photo = favoriteArrayList[position]
            val hitJson = photo.hit

            val gson = Gson()
            val hit = gson.fromJson(hitJson, CommonPic::class.java)

            val intent = Intent(context, PicDetailActivity::class.java)
            try {
                intent.putExtra(PicDetailActivity.EXTRA_PIC, hit)
            } catch (e: ArrayIndexOutOfBoundsException) {
                context.longToast(context.resources.getString(R.string.something_went_wrong))
            }
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        holder.imageView.setOnLongClickListener {
            val photo = favoriteArrayList[position]
            val url = photo.imageUrl
            db = DatabaseHelper(context)

            context.showDialog(context.resources.getString(R.string.del_from_fav_dialog)) {
                deleteFromFavorites(url)
            }
            false
        }

        return row
    }

    private fun deleteFromFavorites(url: String?) {
        if (url != null) {
            db.deleteFromFavorites(url)
        }
        val intent = Intent(context, FavoriteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

}
package com.georgcantor.wallpaperapp.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.db.Favorite
import com.georgcantor.wallpaperapp.ui.FavoriteActivity
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FavoriteAdapter(private val context: Context,
                      private val layout: Int,
                      private val favoriteArrayList: ArrayList<Favorite>) : BaseAdapter() {

    private var db: DatabaseHelper? = null

    override fun getCount(): Int {
        return favoriteArrayList.size
    }

    override fun getItem(position: Int): Any {
        return favoriteArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private inner class ViewHolder {
        internal var imageView: ImageView? = null
        internal var textView: TextView? = null
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View? {
        var row: View? = view
        var holder = ViewHolder()

        if (row == null) {
            val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(layout, null)

            if (row != null) {
                holder.textView = row.findViewById(R.id.timestamp)
                holder.imageView = row.findViewById(R.id.imgFavorite)
                row.tag = holder
            }
        } else {
            holder = row.tag as ViewHolder
        }

        val favorite = favoriteArrayList[position]
        holder.textView?.text = formatDate(favorite.timestamp)
        Picasso.with(context)
                .load(favorite.imageUrl)
                .placeholder(R.drawable.plh)
                .into(holder.imageView)

        holder.imageView?.setOnClickListener {
            val activity = context as Activity
            val photo = favoriteArrayList[position]
            val hdUrl = photo.hdUrl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(hdUrl))
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        holder.imageView?.setOnLongClickListener {
            val photo = favoriteArrayList[position]
            val url = photo.imageUrl
            db = DatabaseHelper(context)

            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.del_from_fav_dialog)

            builder.setPositiveButton(R.string.yes) { _, _ ->
                if (url != null) {
                    db?.deleteFromFavorites(url)
                }
                val intent = Intent(context, FavoriteActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }

            builder.setNeutralButton(R.string.cancel_dialog) { _, _ -> }

            builder.setNegativeButton(R.string.no) { _, _ -> }
            builder.create().show()
            false
        }

        return row
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(dateStr: String?): String {
        try {
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = fmt.parse(dateStr)
            val fmtOut = SimpleDateFormat("MMM d")
            return fmtOut.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }
}
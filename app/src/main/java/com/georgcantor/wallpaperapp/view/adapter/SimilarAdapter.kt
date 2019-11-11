package com.georgcantor.wallpaperapp.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.view.activity.DetailsActivity
import com.georgcantor.wallpaperapp.util.longToast
import kotlinx.android.synthetic.main.similar_item.view.similarImageView

class SimilarAdapter(private val context: Context) : RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {

    private val similarList: MutableList<CommonPic>?
    private lateinit var activity: DetailsActivity

    init {
        this.similarList = ArrayList()
    }

    fun setList(similarList: ArrayList<CommonPic>, activity: DetailsActivity) {
        this.activity = activity
        this.similarList?.addAll(similarList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.similar_item, parent, false)

        return SimilarViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {

        context.loadImage(
            similarList?.get(position)?.url ?: "",
            context.resources.getDrawable(R.drawable.plh),
            holder.imageView,
            null
        )

        holder.itemView.setOnClickListener {
            val activity = context as Activity
            val intent = Intent(context, DetailsActivity::class.java)
            try {
                intent.putExtra(DetailsActivity.EXTRA_PIC, similarList?.get(position)?.heght?.let { height ->
                    CommonPic(
                        url = similarList[position].url,
                        width = similarList[position].width,
                        heght = height,
                        likes = similarList[position].likes,
                        favorites = similarList[position].favorites,
                        tags = similarList[position].tags,
                        downloads = similarList[position].downloads,
                        imageURL = similarList[position].imageURL,
                        fullHDURL = similarList[position].fullHDURL,
                        user = similarList[position].user,
                        userImageURL = similarList[position].userImageURL
                    )
                })
            } catch (e: ArrayIndexOutOfBoundsException) {
                context.longToast(context.getString(R.string.something_went_wrong))
            }
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }
    }

    override fun getItemCount(): Int = similarList?.size ?: 0

    class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.similarImageView
    }

}
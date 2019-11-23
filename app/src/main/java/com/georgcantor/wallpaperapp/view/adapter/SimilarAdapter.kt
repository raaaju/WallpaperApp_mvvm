package com.georgcantor.wallpaperapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.openActivity
import com.georgcantor.wallpaperapp.view.activity.DetailsActivity
import com.georgcantor.wallpaperapp.view.activity.DetailsActivity.Companion.EXTRA_PIC
import kotlinx.android.synthetic.main.similar_item.view.*

class SimilarAdapter(private val context: Context) :
    RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder =
            SimilarViewHolder(LayoutInflater.from(context).inflate(R.layout.similar_item, null))

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        context.loadImage(
            similarList?.get(position)?.url ?: "",
            context.resources.getDrawable(R.drawable.placeholder),
            holder.imageView,
            null
        )

        holder.itemView.setOnClickListener {
            context.openActivity(DetailsActivity::class.java) {
                putParcelable(
                    EXTRA_PIC,
                    similarList?.get(position)?.heght?.let { height ->
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
                    }
                )
            }
        }
    }

    override fun getItemCount(): Int = similarList?.size ?: 0

    class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.similarImageView
    }

}
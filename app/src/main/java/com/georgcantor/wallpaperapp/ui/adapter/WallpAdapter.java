package com.georgcantor.wallpaperapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Hit;
import com.georgcantor.wallpaperapp.model.Pic;
import com.georgcantor.wallpaperapp.ui.PicDetailActivity;
import com.georgcantor.wallpaperapp.ui.util.WallpViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WallpAdapter extends RecyclerView.Adapter<WallpViewHolder> {

    private List<Hit> hit;
    private Context context;

    public WallpAdapter(Context context) {
        this.context = context;
        this.hit = new ArrayList<>();
    }

    @NonNull
    @Override
    public WallpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.wallp_item, null);
        final WallpViewHolder wallpViewHolder = new WallpViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                int position = wallpViewHolder.getAdapterPosition();
                Intent intent = new Intent(context, PicDetailActivity.class);
                intent.putExtra(PicDetailActivity.EXTRA_PIC, hit.get(position));
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        return wallpViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WallpViewHolder holder, int position) {
        Hit photo = this.hit.get(position);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                holder.discWallp.getLayoutParams();
        float height = photo.getPreviewHeight();
        float width = photo.getPreviewWidth();
        float ratio = height / width;
        layoutParams.height = (int) (layoutParams.width * ratio);
        holder.discWallp.setLayoutParams(layoutParams);
        holder.discWallp.setRatio(ratio);

        Picasso.with(context)
                .load(photo.getWebformatURL())
                .placeholder(R.drawable.plh)
                .into(holder.discWallp);
    }

    @Override
    public int getItemCount() {
        return (hit == null) ? 0 : hit.size();
    }

    public void setPicList(Pic picList) {
        if (picList.getHits() != null)
            this.hit.addAll(picList.getHits());
        notifyDataSetChanged();
    }
}

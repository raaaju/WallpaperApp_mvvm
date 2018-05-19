package com.georgcantor.wallpaperapp.ui.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.georgcantor.wallpaperapp.R;

public class ExploreViewHolder extends RecyclerView.ViewHolder {

    public ImageView category;
    public TextView category_name;

    public ExploreViewHolder(View itemView) {
        super(itemView);
        category = itemView.findViewById(R.id.explore_view);
        category_name = itemView.findViewById(R.id.exptext);
    }
}

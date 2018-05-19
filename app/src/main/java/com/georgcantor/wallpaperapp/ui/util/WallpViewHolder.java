package com.georgcantor.wallpaperapp.ui.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.georgcantor.wallpaperapp.R;

public class WallpViewHolder extends RecyclerView.ViewHolder {

    public DynamicHeightImageView discWallp;

    public WallpViewHolder(View itemView) {
        super(itemView);
        discWallp = itemView.findViewById(R.id.wallpView);
    }
}

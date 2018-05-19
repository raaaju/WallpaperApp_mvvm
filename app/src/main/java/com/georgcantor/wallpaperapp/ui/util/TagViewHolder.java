package com.georgcantor.wallpaperapp.ui.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.georgcantor.wallpaperapp.R;

public class TagViewHolder extends RecyclerView.ViewHolder {

    public TextView tag;

    public TagViewHolder(View itemView) {
        super(itemView);
        tag = (TextView) itemView.findViewById(R.id.tag_item);
    }
}

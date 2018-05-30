package com.georgcantor.wallpaperapp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Hit;
import com.georgcantor.wallpaperapp.ui.util.WallpViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<WallpViewHolder> {

    private List<Hit> hit;
    private Context context;
    public int width;
    public int height;

    public CollectionAdapter(Context context) {
        this.context = context;
        this.hit = new ArrayList<>();
    }

    @NonNull
    @Override
    public WallpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.wallp_item, null);
        final WallpViewHolder rcv = new WallpViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

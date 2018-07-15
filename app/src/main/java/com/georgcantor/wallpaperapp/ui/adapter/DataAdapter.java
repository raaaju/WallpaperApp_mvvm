package com.georgcantor.wallpaperapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Hit;
import com.georgcantor.wallpaperapp.ui.PicDetailActivity;
import com.georgcantor.wallpaperapp.ui.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<Hit> hits;
    private Context mContext;

    public DataAdapter(Context mContext, List<Hit> hits) {
        this.mContext = mContext;
        this.hits = hits;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallp_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Hit hit = hits.get(position);

        Picasso.with(mContext)
                .load(hit.getWebformatURL())
                .into(holder.discWallp);

        holder.discWallp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, PicDetailActivity.class);
                intent.putExtra(PicDetailActivity.EXTRA_PIC, hits.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public DynamicHeightImageView discWallp;

        public ViewHolder(View view) {
            super(view);
            discWallp = view.findViewById(R.id.wallpView);
        }
    }
}

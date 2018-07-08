package com.georgcantor.wallpaperapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Hit;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<Hit> hits;
    private Context mContext;
    private int lastPosition = -1;

    public DataAdapter(Context mContext, List<Hit> hits) {
        this.mContext = mContext;
        this.hits = hits;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Hit hit = hits.get(position);

        Picasso.with(mContext)
                .load(hit.getWebformatURL())
                .into(holder.img_card_main);

        holder.img_card_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(hits.get(position).getImageURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return hits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_card_main;

        public ViewHolder(View view) {
            super(view);
            img_card_main = view.findViewById(R.id.img_card_main);
        }
    }
}

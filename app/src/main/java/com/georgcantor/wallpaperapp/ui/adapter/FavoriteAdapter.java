package com.georgcantor.wallpaperapp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.db.Favorite;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private Context context;
    private List<Favorite> favoriteList;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_list_row, parent, false);
        final MyViewHolder rcv = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = rcv.getAdapterPosition();
                Favorite photo = favoriteList.get(position);
                String url = photo.getImageUrl();
                Toast.makeText(context, url, Toast.LENGTH_LONG).show();
            }
        });
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);
        holder.timestamp.setText(formatDate(favorite.getTimestamp()));
        Picasso.with(context)
                .load(favorite.getImageUrl())
                .placeholder(R.drawable.plh)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView timestamp;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgFavorite);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}

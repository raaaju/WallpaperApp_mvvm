package com.georgcantor.wallpaperapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper;
import com.georgcantor.wallpaperapp.model.db.Favorite;
import com.georgcantor.wallpaperapp.ui.PicDetailActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FavoriteAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Favorite> favoriteArrayList;
    private DatabaseHelper db;

    public FavoriteAdapter(Context context, int layout, ArrayList<Favorite> favoriteArrayList) {
        this.context = context;
        this.layout = layout;
        this.favoriteArrayList = favoriteArrayList;
    }

    @Override
    public int getCount() {
        return favoriteArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return favoriteArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    @Override
    public View getView(final int position, final View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.textView = row.findViewById(R.id.timestamp);
            holder.imageView = row.findViewById(R.id.imgFavorite);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Favorite favorite = favoriteArrayList.get(position);
        holder.textView.setText(formatDate(favorite.getTimestamp()));
        Picasso.with(context)
                .load(favorite.getImageUrl())
                .placeholder(R.drawable.plh)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorite photo = favoriteArrayList.get(position);
                final String hdUrl = photo.getHdUrl();
                Intent intent = new Intent(context, PicDetailActivity.class);
                intent.putExtra(PicDetailActivity.EXTRA_PIC, hdUrl);
                context.startActivity(intent);
            }
        });

//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Favorite photo = favoriteArrayList.get(position);
//                final String url = photo.getImageUrl();
//                db = new DatabaseHelper(context);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setMessage(R.string.del_from_fav_dialog);
//
//                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        db.deleteFromFavorites(url);
//                        Intent intent = new Intent(context, FavoriteActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        context.startActivity(intent);
//                    }
//                });
//
//                builder.setNeutralButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                });
//
//                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                });
//                builder.create().show();
//            }
//        });

        return row;
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
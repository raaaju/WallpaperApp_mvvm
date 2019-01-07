package com.georgcantor.wallpaperapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Category;
import com.georgcantor.wallpaperapp.ui.SelectCatActivity;
import com.georgcantor.wallpaperapp.ui.util.ExploreViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<ExploreViewHolder> {

    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(Context context) {
        this.context = context;
        this.categoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_explore, null);
        final ExploreViewHolder rcv = new ExploreViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                int position = rcv.getAdapterPosition();
                Intent intent = new Intent(context, SelectCatActivity.class);
                intent.putExtra(SelectCatActivity.EXTRA_CAT,
                        categoryList.get(position).getCategoryDrawId());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        Category category = categoryList.get(position);
        int id = context.getResources().getIdentifier(context.getResources()
                        .getString(R.string.package_drawable) + category.getCategoryDrawId(),
                null, null);
        holder.getCategoryName().setText(category.getCategoryName());
        holder.getCategory().setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return (categoryList == null) ? 0 : categoryList.size();
    }

    public void setCategoryList(List<Category> categories) {
        if (categories != null) {
            this.categoryList.clear();
            this.categoryList.addAll(categories);
            notifyDataSetChanged();
        }
    }
}

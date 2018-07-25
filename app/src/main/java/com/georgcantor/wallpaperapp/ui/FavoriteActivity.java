package com.georgcantor.wallpaperapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper;
import com.georgcantor.wallpaperapp.model.db.Favorite;
import com.georgcantor.wallpaperapp.ui.adapter.FavoriteAdapter;
import com.georgcantor.wallpaperapp.ui.util.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private List<Favorite> favoriteList = new ArrayList<>();
    private DatabaseHelper db;
    private TextView textViewNoFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_fav);
        textViewNoFav = findViewById(R.id.empty_fav_tv);

        db = new DatabaseHelper(this);

        favoriteList.addAll(db.getAllFavorites());

        FavoriteAdapter adapter = new FavoriteAdapter(this, favoriteList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapter);

        toggleEmptyHistory();
    }

    private void toggleEmptyHistory() {
        if (db.getHistoryCount() > 0) {
            textViewNoFav.setVisibility(View.GONE);
        } else {
            textViewNoFav.setVisibility(View.VISIBLE);
        }
    }
}

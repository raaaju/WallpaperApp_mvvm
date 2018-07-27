package com.georgcantor.wallpaperapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper;
import com.georgcantor.wallpaperapp.model.db.Favorite;
import com.georgcantor.wallpaperapp.ui.adapter.FavoriteAdapter;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private ArrayList<Favorite> list;
    private DatabaseHelper db;
    private TextView textViewNoFav;
    private GridView gridView;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        gridView = findViewById(R.id.gridViewFav);
        textViewNoFav = findViewById(R.id.empty_fav_tv);


        db = new DatabaseHelper(this);
        list = new ArrayList<>();
        list.addAll(db.getAllFavorites());

        adapter = new FavoriteAdapter(this, R.layout.favorite_list_row, list);
        gridView.setAdapter(adapter);

        toggleEmptyHistory();
    }

    private void toggleEmptyHistory() {
        if (db.getHistoryCount() > 0) {
            textViewNoFav.setVisibility(View.GONE);
        } else {
            textViewNoFav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}

package com.georgcantor.wallpaperapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Pic;
import com.georgcantor.wallpaperapp.network.AsyncResponse;
import com.georgcantor.wallpaperapp.network.NetworkUtilities;
import com.georgcantor.wallpaperapp.network.WallpService;
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter;
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener;

public class SelectCategory extends AppCompatActivity implements AsyncResponse {

    public static final String EXTRA_CAT = "category";
    public WallpAdapter catAdapter;
    public RecyclerView recyclerView_cat;
    public NetworkUtilities networkUtilities;
    private String type;
    public int column_no;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkUtilities = new NetworkUtilities(this);
        type = getIntent().getStringExtra(EXTRA_CAT);
        if (networkUtilities.isInternetConnectionPresent()) {
            setContentView(R.layout.activity_select_category);
            Toolbar toolbar = findViewById(R.id.toolbar_category);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(type);
            loadNextDataFromApi(1);
            recyclerView_cat = findViewById(R.id.SelCatRecView);
            recyclerView_cat.setHasFixedSize(true);

            checkScreenSize();
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(column_no, StaggeredGridLayoutManager.VERTICAL);
            recyclerView_cat.setLayoutManager(staggeredGridLayoutManager);
            EndlessRecyclerViewScrollListener scrollListener_cat =
                    new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            loadNextDataFromApi(page);
                        }
                    };
            recyclerView_cat.addOnScrollListener(scrollListener_cat);
            catAdapter = new WallpAdapter(this);
            recyclerView_cat.setAdapter(catAdapter);
        } else
            setContentView(R.layout.fragment_no_internet);
    }

    @Override
    public void processFinish(Pic output) {
        if (output.getHits() != null) {
            catAdapter.setPicList(output);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void loadNextDataFromApi(int offset) {
        final WallpService wallpService =
                new WallpService(networkUtilities, this, this, offset, type);
        wallpService.loadWallp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkScreenSize() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                column_no = 4;
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                column_no = 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                column_no = 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                column_no = 2;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                column_no = 2;
                break;
            default:
                column_no = 2;
        }
    }
}

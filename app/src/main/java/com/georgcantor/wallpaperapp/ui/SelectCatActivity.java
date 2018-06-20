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

public class SelectCatActivity extends AppCompatActivity implements AsyncResponse {

    public static final String EXTRA_CAT = "category";
    public WallpAdapter catAdapter;
    public RecyclerView recyclerViewCat;
    public NetworkUtilities networkUtilities;
    private String type;
    public int columnNo;

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
            recyclerViewCat = findViewById(R.id.SelCatRecView);
            recyclerViewCat.setHasFixedSize(true);

            checkScreenSize();
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(columnNo, StaggeredGridLayoutManager.VERTICAL);
            recyclerViewCat.setLayoutManager(staggeredGridLayoutManager);
            EndlessRecyclerViewScrollListener scrollListener_cat =
                    new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            loadNextDataFromApi(page);
                        }
                    };
            recyclerViewCat.addOnScrollListener(scrollListener_cat);
            catAdapter = new WallpAdapter(this);
            recyclerViewCat.setAdapter(catAdapter);
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
                columnNo = 4;
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                columnNo = 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                columnNo = 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                columnNo = 2;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                columnNo = 2;
                break;
            default:
                columnNo = 2;
        }
    }
}

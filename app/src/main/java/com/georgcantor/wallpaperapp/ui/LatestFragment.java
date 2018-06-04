package com.georgcantor.wallpaperapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Pic;
import com.georgcantor.wallpaperapp.network.AsyncResponse;
import com.georgcantor.wallpaperapp.network.NetworkUtilities;
import com.georgcantor.wallpaperapp.network.WallpService;
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter;
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener;

public class LatestFragment extends Fragment implements AsyncResponse {

    public WallpAdapter wallpAdapter;
    public RecyclerView recyclerView;
    public NetworkUtilities networkUtilities;
    public EndlessRecyclerViewScrollListener scrollListener;
    public WallpService wallpService;
    public int column_no;

    public LatestFragment() {
    }

    public static LatestFragment newInstance() {
        LatestFragment fragment = new LatestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkUtilities = new NetworkUtilities(getActivity());

        loadNextDataFromApi(1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (!networkUtilities.isInternetConnectionPresent()) {
            return inflater.inflate(R.layout.fragment_no_internet, container, false);
        } else {
            View view = inflater.inflate(R.layout.fragment_latest, container, false);
            recyclerView = view.findViewById(R.id.latestRecView);
            recyclerView.setHasFixedSize(true);

            checkScreenSize();
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(column_no, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextDataFromApi(page);
                }
            };
            scrollListener.resetState();
            recyclerView.addOnScrollListener(scrollListener);
            wallpAdapter = new WallpAdapter(getActivity());
            recyclerView.setAdapter(wallpAdapter);
            return view;
        }
    }

    @Override
    public void processFinish(Pic output) {
        if (output.getHits() != null) {
            wallpAdapter.setPicList(output);
        }
    }

    private void loadNextDataFromApi(int offset) {
        String type = getResources().getString(R.string.latest_small);
        wallpService = new WallpService(networkUtilities, getActivity(), this, offset, type);
        wallpService.loadWallp();
    }

    private void checkScreenSize() {
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

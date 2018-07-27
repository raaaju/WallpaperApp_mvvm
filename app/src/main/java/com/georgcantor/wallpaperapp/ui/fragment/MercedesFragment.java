package com.georgcantor.wallpaperapp.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.georgcantor.wallpaperapp.MyApplication;
import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Pic;
import com.georgcantor.wallpaperapp.network.ApiClient;
import com.georgcantor.wallpaperapp.network.ApiService;
import com.georgcantor.wallpaperapp.network.NetworkUtilities;
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor;
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor;
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter;
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MercedesFragment extends Fragment {

    private WallpAdapter wallpAdapter;
    private NetworkUtilities networkUtilities;
    private int column_no;
    private Pic picResult = new Pic();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MercedesFragment() {
    }

    public static MercedesFragment newInstance() {
        MercedesFragment fragment = new MercedesFragment();
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

        View view = inflater.inflate(R.layout.fragment_mercedes, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.mercRecView);
        recyclerView.setHasFixedSize(true);

        ImageView ivNoInternet = view.findViewById(R.id.iv_no_internet);
        if (!networkUtilities.isInternetConnectionPresent()) {
            ivNoInternet.setVisibility(View.VISIBLE);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNextDataFromApi(1);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        checkScreenSize();
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(column_no, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
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

    private void loadNextDataFromApi(int index) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addNetworkInterceptor(new ResponseCacheInterceptor());
        httpClient.addInterceptor(new OfflineResponseCacheInterceptor());
        httpClient.cache(new Cache(new File(MyApplication.getInstance()
                .getCacheDir(), "ResponsesCache"), 10 * 1024 * 1024));
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);

        ApiService client = ApiClient.getClient(httpClient).create(ApiService.class);
        Call<Pic> call;
        call = client.getMercedesPic(index);
        call.enqueue(new Callback<Pic>() {
            @Override
            public void onResponse(Call<Pic> call, Response<Pic> response) {
                try {
                    if (!response.isSuccessful()) {
                        Log.d(getContext().getResources().getString(R.string.No_Success),
                                response.errorBody().string());
                    } else {
                        picResult = response.body();
                        if (picResult != null) {
                            wallpAdapter.setPicList(picResult);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Pic> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getResources()
                        .getString(R.string.wrong_message), Toast.LENGTH_SHORT).show();
            }
        });
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

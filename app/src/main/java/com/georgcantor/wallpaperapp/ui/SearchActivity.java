package com.georgcantor.wallpaperapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 111;
    private static final int PERMISSION_REQUEST_CODE = 222;
    private EditText mEdtSearch;
    private TextView mTxvNoResultsFound;
    private SwipeRefreshLayout mSwipeRefreshSearch;
    public NetworkUtilities networkUtilities;
    public int columnNo;
    private Pic picResult = new Pic();
    public WallpAdapter wallpAdapter;
    public int index = 1;
    private boolean voiceInvisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkUtilities = new NetworkUtilities(this);
        setContentView(R.layout.activity_search);
        mEdtSearch = findViewById(R.id.editText_search);
        mTxvNoResultsFound = findViewById(R.id.tv_no_results);
        mSwipeRefreshSearch = findViewById(R.id.swipe_refresh_layout_search);

        createToolbar();

        initViews();

        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager mgr =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    searchEverything(mEdtSearch.getText().toString().trim(), index);
                    return true;
                }
                return false;
            }
        });
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);

        checkScreenSize();
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnNo, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener_cat =
                new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        searchEverything(mEdtSearch.getText().toString().trim(), page);
                    }
                };
        recyclerView.addOnScrollListener(scrollListener_cat);
        wallpAdapter = new WallpAdapter(this);
        recyclerView.setAdapter(wallpAdapter);
    }

    public void searchEverything(final String search, int index) {
        mSwipeRefreshSearch.setEnabled(true);
        mSwipeRefreshSearch.setRefreshing(true);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addNetworkInterceptor(new ResponseCacheInterceptor());
        httpClient.addInterceptor(new OfflineResponseCacheInterceptor());
        httpClient.cache(new Cache(new File(SearchActivity.this
                .getCacheDir(), "ResponsesCache"), 10 * 1024 * 1024));
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);

        ApiService client = ApiClient.getClient(httpClient).create(ApiService.class);
        Call<Pic> call;
        call = client.getSearchResults(search, index);
        call.enqueue(new Callback<Pic>() {
            @Override
            public void onResponse(Call<Pic> call, Response<Pic> response) {
                try {
                    if (!response.isSuccessful()) {
                        Log.d(getResources().getString(R.string.No_Success),
                                response.errorBody().string());
                    } else {
                        picResult = response.body();
                        if (picResult != null) {
                            wallpAdapter.setPicList(picResult);
                            mTxvNoResultsFound.setVisibility(View.GONE);
                            mSwipeRefreshSearch.setRefreshing(false);
                            mSwipeRefreshSearch.setEnabled(false);
                        }
                        invalidateOptionsMenu();
                        voiceInvisible = true;
                        mEdtSearch.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Pic> call, Throwable t) {
                Toast.makeText(SearchActivity.this, getResources()
                        .getString(R.string.wrong_message), Toast.LENGTH_SHORT).show();
                mSwipeRefreshSearch.setRefreshing(false);
                mSwipeRefreshSearch.setEnabled(false);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                resetActivity();
                break;
            case R.id.action_voice_search:
                checkPermission();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_voice_search);
        item.setVisible(!voiceInvisible);
        return true;
    }

    private void resetActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                requestAudioPermission();
            } else {
                speak();
            }
        } else {
            speak();
        }
    }

    public void speak() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_something));
            startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestAudioPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speak();
            } else {
                startPermissionDialog();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.allow_app_use_mic);
        builder.setPositiveButton(getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        finish();
                    }
                });

        builder.setNegativeButton(getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.setNeutralButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchEverything(String.valueOf(arrayList), index);
            mEdtSearch.setText(String.valueOf(arrayList));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}

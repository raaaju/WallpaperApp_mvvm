package com.georgcantor.wallpaperapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.georgcantor.wallpaperapp.MyApplication;
import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Hit;
import com.georgcantor.wallpaperapp.model.Pic;
import com.georgcantor.wallpaperapp.network.ApiClient;
import com.georgcantor.wallpaperapp.network.ApiService;
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor;
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor;
import com.georgcantor.wallpaperapp.ui.adapter.DataAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private RecyclerView mRecyclerViewSearch;
    private List<Hit> hits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        createToolbar();
        initViews();

        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager mgr =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    searchEverything(mEdtSearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mSwipeRefreshSearch.setEnabled(false);
        mSwipeRefreshSearch.setColorSchemeResources(R.color.colorPrimary);
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
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initViews() {
        mEdtSearch = findViewById(R.id.editText_search);
        mSwipeRefreshSearch = findViewById(R.id.swipe_refresh_layout_search);
        mRecyclerViewSearch = findViewById(R.id.search_recycler_view);
        mTxvNoResultsFound = findViewById(R.id.tv_no_results);
        mRecyclerViewSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
    }

    private void searchEverything(final String search) {
        mSwipeRefreshSearch.setEnabled(true);
        mSwipeRefreshSearch.setRefreshing(true);

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
        call = client.getSearchResults(search, 1);
        call.enqueue(new Callback<Pic>() {
            @Override
            public void onResponse(@NonNull Call<Pic> call, @NonNull Response<Pic> response) {
                if (response.isSuccessful() && response.body().getHits() != null) {
                    if (response.body().getTotal() != 0) {
                        if (!hits.isEmpty()) {
                            hits.clear();
                        }
                        hits = response.body().getHits();
                        mRecyclerViewSearch.setVisibility(View.VISIBLE);
                        mTxvNoResultsFound.setVisibility(View.GONE);
                        mRecyclerViewSearch.setAdapter(new DataAdapter(getApplicationContext(), hits));
                        mSwipeRefreshSearch.setRefreshing(false);
                        mSwipeRefreshSearch.setEnabled(false);
                    } else if (response.body().getTotal() == 0) {
                        mSwipeRefreshSearch.setRefreshing(false);
                        mSwipeRefreshSearch.setEnabled(false);
                        mTxvNoResultsFound.setVisibility(View.VISIBLE);
                        mRecyclerViewSearch.setVisibility(View.GONE);
                        mTxvNoResultsFound.setText("No Results found for \"" + search + "\".");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pic> call, @NonNull Throwable t) {
                mSwipeRefreshSearch.setRefreshing(false);
                mSwipeRefreshSearch.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                mEdtSearch.setText("");
                mEdtSearch.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mgr.showSoftInput(mEdtSearch, InputMethodManager.SHOW_IMPLICIT);
                mRecyclerViewSearch.setVisibility(View.GONE);
                break;
            case R.id.action_voice_search:
                checkPermission();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
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
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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
            searchEverything(String.valueOf(arrayList));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

package com.georgcantor.wallpaperapp.ui

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.network.ApiClient
import com.georgcantor.wallpaperapp.network.ApiService
import com.georgcantor.wallpaperapp.network.NetworkUtilities
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import kotlinx.android.synthetic.main.activity_fetch.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.TimeUnit

class FetchActivity : AppCompatActivity() {

    companion object {
        const val FETCH_TYPE = "fetch_type"
    }

    lateinit var catAdapter: WallpAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var networkUtilities: NetworkUtilities
    private var type: String? = null
    var columnNo: Int = 0

    private var picResult: Pic? = Pic()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkUtilities = NetworkUtilities(this)
        type = intent.getStringExtra(FETCH_TYPE)
        setContentView(R.layout.activity_fetch)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_fetch)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = type

        if (!UtilityMethods.isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show()
        }

        loadNextDataFromApi(1)
        recyclerView = findViewById(R.id.fetchRecView)
        recyclerView.setHasFixedSize(true)

        checkScreenSize()

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(columnNo,
                StaggeredGridLayoutManager.VERTICAL)

        recyclerView.layoutManager = staggeredGridLayoutManager

        val listener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadNextDataFromApi(page)
            }
        }
        recyclerView.addOnScrollListener(listener)
        catAdapter = WallpAdapter(this)
        recyclerView.adapter = catAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    fun loadNextDataFromApi(index: Int) {
        progressFetch?.let { it.visibility = View.VISIBLE }
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(ResponseCacheInterceptor())
        httpClient.addInterceptor(OfflineResponseCacheInterceptor())
        httpClient.cache(Cache(File(this@FetchActivity
                .cacheDir, "ResponsesCache"), (10 * 1024 * 1024).toLong()))
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)

        val client = ApiClient.getClient(httpClient).create(ApiService::class.java)
        val call: Call<Pic>
        call = client.getSearchResults(type, index)
        call.enqueue(object : Callback<Pic> {
            override fun onResponse(call: Call<Pic>, response: Response<Pic>) {
                progressFetch?.let { it.visibility = View.GONE }
                try {
                    if (!response.isSuccessful) {
                        Log.d(resources.getString(R.string.No_Success),
                                response.errorBody()?.string())
                    } else {
                        picResult = response.body()
                        if (picResult != null) {
                            catAdapter.setPicList(picResult?.hits as MutableList<Hit>)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<Pic>, t: Throwable) {
                progressFetch?.let { it.visibility = View.GONE }
                Toast.makeText(this@FetchActivity, resources
                        .getString(R.string.wrong_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkScreenSize() {
        val screenSize = resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK

        columnNo = when (screenSize) {
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> 4
            Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> 3
            Configuration.SCREENLAYOUT_SIZE_LARGE -> 3
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> 2
            Configuration.SCREENLAYOUT_SIZE_SMALL -> 2
            else -> 2
        }
    }
}
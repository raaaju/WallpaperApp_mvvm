package com.georgcantor.wallpaperapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.network.ApiClient
import com.georgcantor.wallpaperapp.network.ApiService
import com.georgcantor.wallpaperapp.network.NetworkUtilities
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_results.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 111
        private const val PERMISSION_REQUEST_CODE = 222
    }

    private lateinit var networkUtilities: NetworkUtilities
    private var columnNo: Int = 0
    private var picResult: Pic? = Pic()
    lateinit var wallpAdapter: WallpAdapter
    private var index = 1

    private var voiceInvisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        networkUtilities = NetworkUtilities(this)
        setContentView(R.layout.activity_search)

        createToolbar()
        initViews()

        editText_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(v.windowToken, 0)
                searchEverything(editText_search.text.toString().trim { it <= ' ' }, index)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun createToolbar() {
        setSupportActionBar(toolbar_search)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        toolbar_search.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back)
        toolbar_search.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun initViews() {
        search_recycler_view.setHasFixedSize(true)
        checkScreenSize()

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(columnNo,
                StaggeredGridLayoutManager.VERTICAL)
        search_recycler_view.layoutManager = staggeredGridLayoutManager

        val listener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                searchEverything(editText_search.text.toString().trim { it <= ' ' }, page)
            }
        }
        search_recycler_view.addOnScrollListener(listener)
        wallpAdapter = WallpAdapter(this)
        search_recycler_view.adapter = wallpAdapter
    }

    fun searchEverything(search: String, index: Int) {
        swipe_refresh_layout_search.isEnabled = true
        swipe_refresh_layout_search.isRefreshing = true
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(ResponseCacheInterceptor())
        httpClient.addInterceptor(OfflineResponseCacheInterceptor())
        httpClient.cache(Cache(File(this@SearchActivity
                .cacheDir, "ResponsesCache"), (10 * 1024 * 1024).toLong()))
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)

        val client = ApiClient.getClient(httpClient)?.create(ApiService::class.java)
        val call: Call<Pic>
        client?.let {
            call = it.getSearchResults(search, index)
            call.enqueue(object : Callback<Pic> {

                override fun onResponse(call: Call<Pic>, response: Response<Pic>) {
                    try {
                        if (!response.isSuccessful) {
                            Log.d(resources.getString(R.string.No_Success),
                                    response.errorBody()?.string())
                        } else {
                            picResult = response.body()
                            picResult?.let {
                                wallpAdapter.setPicList(it.hits)
                                tv_no_results.visibility = View.GONE
                                swipe_refresh_layout_search.isRefreshing = false
                                swipe_refresh_layout_search.isEnabled = false
                            }
                            invalidateOptionsMenu()
                            voiceInvisible = true
                            editText_search.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<Pic>, t: Throwable) {
                    Toast.makeText(this@SearchActivity, resources
                            .getString(R.string.wrong_message), Toast.LENGTH_SHORT).show()
                    swipe_refresh_layout_search.isRefreshing = false
                    swipe_refresh_layout_search.isEnabled = false
                }
            })
        }
    }

    private fun checkScreenSize() {
        val screenSize = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        columnNo = when (screenSize) {
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> 4
            Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> 3
            Configuration.SCREENLAYOUT_SIZE_LARGE -> 3
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> 2
            Configuration.SCREENLAYOUT_SIZE_SMALL -> 2
            else -> 2
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cancel -> resetActivity()
            R.id.action_voice_search -> checkPermission()
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.action_voice_search)
        item.isVisible = !voiceInvisible

        return true
    }

    private fun resetActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestAudioPermission()
            } else {
                speak()
            }
        } else {
            speak()
        }
    }

    private fun speak() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_something))
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speak()
            } else {
                startPermissionDialog()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.allow_app_use_mic)
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName"))
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
        builder.setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            searchEverything(arrayList.toString(), index)
            editText_search.setText(arrayList.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}

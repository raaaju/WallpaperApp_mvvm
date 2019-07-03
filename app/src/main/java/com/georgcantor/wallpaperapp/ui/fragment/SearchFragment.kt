package com.georgcantor.wallpaperapp.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.network.ApiClient
import com.georgcantor.wallpaperapp.network.ApiService
import com.georgcantor.wallpaperapp.network.NetworkUtils
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.search_results.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 111
        private const val PERMISSION_REQUEST_CODE = 222
    }

    private lateinit var networkUtils: NetworkUtils
    private var columnNo: Int = 0
    private var picResult: Pic? = Pic()
    lateinit var adapter: WallpAdapter
    private var index = 1

    private var voiceInvisible = false

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        networkUtils = NetworkUtils(requireContext())

        createToolbar()
        initViews()

        searchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val manager = requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, 0)
                searchEverything(searchEditText.text.toString().trim { it <= ' ' }, index)

                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onPause() {
        super.onPause()
        requireActivity().appBarMain.visibility = View.VISIBLE
    }

    private fun createToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(searchToolbar)
        if ((activity as AppCompatActivity).supportActionBar != null) {
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        searchToolbar.navigationIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_arrow_back,
                null
        )
        searchToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initViews() {
        searchRecyclerView.setHasFixedSize(true)
        checkScreenSize()

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(columnNo,
                StaggeredGridLayoutManager.VERTICAL)
        searchRecyclerView.layoutManager = staggeredGridLayoutManager

        val listener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                searchEverything(searchEditText.text.toString().trim { it <= ' ' }, page)
            }
        }
        searchRecyclerView.addOnScrollListener(listener)
        adapter = WallpAdapter(requireContext(), requireFragmentManager())
        searchRecyclerView.adapter = adapter
    }

    fun searchEverything(search: String, index: Int) {
        searchRefreshLayout.isEnabled = true
        searchRefreshLayout.isRefreshing = true
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(ResponseCacheInterceptor())
        httpClient.addInterceptor(OfflineResponseCacheInterceptor())
        httpClient.cache(Cache(File(requireActivity().cacheDir, "ResponsesCache"), (10 * 1024 * 1024).toLong()))
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)

        val client = ApiClient.getClient(httpClient).create(ApiService::class.java)
        val call: Call<Pic>
        call = client.getSearchResults(search, index)
        call.enqueue(object : Callback<Pic> {

            override fun onResponse(call: Call<Pic>, response: Response<Pic>) {
                try {
                    if (!response.isSuccessful) {
                        Log.d(resources.getString(R.string.No_Success), response.errorBody()?.string())
                    } else {
                        picResult = response.body()
                        picResult?.let {
                            adapter.setPicList(it.hits)
                            noResultsTextView.visibility = View.GONE
                            searchRefreshLayout.isRefreshing = false
                            searchRefreshLayout.isEnabled = false
                        }
                        voiceInvisible = true
                        searchEditText.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Pic>, t: Throwable) {
                Toast.makeText(requireContext(), resources
                        .getString(R.string.wrong_message), Toast.LENGTH_SHORT).show()
                searchRefreshLayout.isRefreshing = false
                searchRefreshLayout.isEnabled = false
            }
        })
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
}
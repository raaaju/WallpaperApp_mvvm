package com.georgcantor.wallpaperapp.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.network.ApiClient
import com.georgcantor.wallpaperapp.network.ApiService
import com.georgcantor.wallpaperapp.network.NetworkUtilities
import com.georgcantor.wallpaperapp.network.interceptors.OfflineResponseCacheInterceptor
import com.georgcantor.wallpaperapp.network.interceptors.ResponseCacheInterceptor
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.TimeUnit

class BmwFragment : Fragment() {

    private var wallpAdapter: WallpAdapter? = null
    private var networkUtilities: NetworkUtilities? = null
    private var columnNo: Int = 0
    private var picResult: Pic? = Pic()
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkUtilities = NetworkUtilities(activity!!)

        loadNextDataFromApi(1)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_bmw, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.bmwRecView)
        recyclerView.setHasFixedSize(true)

        val ivNoInternet = view.findViewById<ImageView>(R.id.iv_no_internet)
        if (!networkUtilities!!.isInternetConnectionPresent) {
            ivNoInternet.visibility = View.VISIBLE
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout!!.setOnRefreshListener {
            loadNextDataFromApi(1)
            mSwipeRefreshLayout!!.isRefreshing = false
        }

        checkScreenSize()
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(columnNo,
                StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredGridLayoutManager

        val scrollListener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadNextDataFromApi(page)
            }
        }
        scrollListener.resetState()
        recyclerView.addOnScrollListener(scrollListener)
        wallpAdapter = WallpAdapter(activity)
        recyclerView.adapter = wallpAdapter
        return view
    }

    private fun loadNextDataFromApi(index: Int) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(ResponseCacheInterceptor())
        httpClient.addInterceptor(OfflineResponseCacheInterceptor())
        httpClient.cache(Cache(File(MyApplication.getInstance()
                .cacheDir, "ResponsesCache"), (10 * 1024 * 1024).toLong()))
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)

        val client = ApiClient.getClient(httpClient).create(ApiService::class.java)
        val call: Call<Pic>
        call = client.getBmwPic(index)
        call.enqueue(object : Callback<Pic> {
            override fun onResponse(call: Call<Pic>, response: Response<Pic>) {
                try {
                    if (!response.isSuccessful) {
                        Log.d(context!!.resources.getString(R.string.No_Success),
                                response.errorBody()!!.string())
                    } else {
                        picResult = response.body()
                        if (picResult != null) {
                            wallpAdapter!!.setPicList(picResult)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<Pic>, t: Throwable) {
                Toast.makeText(context, context!!.resources
                        .getString(R.string.wrong_message), Toast.LENGTH_SHORT).show()
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

    companion object {

        fun newInstance(): BmwFragment {
            val fragment = BmwFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}

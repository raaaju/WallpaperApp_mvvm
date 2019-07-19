package com.georgcantor.wallpaperapp.ui.fragment

import android.content.Context
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
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.network.ApiService
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import kotlinx.android.synthetic.main.fragment_mercedes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class MercedesFragment : Fragment() {

    companion object {
        fun newInstance(): MercedesFragment {
            val fragment = MercedesFragment()
            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }

    @Inject
    lateinit var retrofit: Retrofit

    private var wallpAdapter: WallpAdapter? = null
    private var columnNo: Int = 0
    private var picResult: Pic? = Pic()

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onAttach(context: Context?) {
        (MyApplication.instance as MyApplication)
                .getApiComponent()
                .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!UtilityMethods.isNetworkAvailable) {
            Toast.makeText(context, getString(R.string.check_internet), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_mercedes, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.mercRecView)
        recyclerView.setHasFixedSize(true)

        val ivNoInternet = view.findViewById<ImageView>(R.id.iv_no_internet)
        if (!UtilityMethods.isNetworkAvailable) {
            ivNoInternet.visibility = View.VISIBLE
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout?.setOnRefreshListener {
            loadData(1)
            mSwipeRefreshLayout?.isRefreshing = false
        }

        checkScreenSize()
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(columnNo, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredGridLayoutManager
        val scrollListener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        recyclerView.addOnScrollListener(scrollListener)
        wallpAdapter = WallpAdapter(requireContext())
        recyclerView.adapter = wallpAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(1)
    }

    private fun loadData(index: Int) {
        progressMain?.let { it.visibility = View.VISIBLE }

        val client = retrofit.create(ApiService::class.java)
        val call: Call<Pic>
        client?.let {
            call = client.getMercedesPic(index)
            call.enqueue(object : Callback<Pic> {
                override fun onResponse(call: Call<Pic>, response: Response<Pic>) {
                    progressMain?.let { it.visibility = View.GONE }
                    try {
                        if (!response.isSuccessful) {
                            Log.d(context?.resources?.getString(R.string.No_Success),
                                    response.errorBody()?.string())
                        } else {
                            picResult = response.body()
                            if (picResult != null) {
                                wallpAdapter?.setPicList(picResult?.hits as MutableList<Hit>)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<Pic>, t: Throwable) {
                    progressMain?.let { it.visibility = View.GONE }
                    Toast.makeText(context, context?.resources?.getString(R.string.wrong_message),
                            Toast.LENGTH_SHORT).show()
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
}

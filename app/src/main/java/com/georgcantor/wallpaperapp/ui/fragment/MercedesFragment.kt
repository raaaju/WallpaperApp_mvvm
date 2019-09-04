package com.georgcantor.wallpaperapp.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.network.ApiService
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener
import com.georgcantor.wallpaperapp.ui.util.HideNavScrollListener
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import kotlinx.android.synthetic.main.content_main.*
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

    override fun onAttach(context: Context) {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_mercedes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!UtilityMethods.isNetworkAvailable) {
            noInternetImageView.visibility = View.VISIBLE
        }

        mercedesRefreshLayout.setOnRefreshListener {
            loadData(1)
            mercedesRefreshLayout.isRefreshing = false
        }
        checkScreenSize()

        val gridLayoutManager = StaggeredGridLayoutManager(columnNo, StaggeredGridLayoutManager.VERTICAL)
        mercedesRecyclerView.setHasFixedSize(true)
        mercedesRecyclerView.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        mercedesRecyclerView.addOnScrollListener(scrollListener)
        wallpAdapter = WallpAdapter(requireContext())
        mercedesRecyclerView.adapter = wallpAdapter

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        mercedesRecyclerView.addOnScrollListener(hideScrollListener)

        loadData(1)
    }

    private fun loadData(index: Int) {
        animationView?.visibility = View.VISIBLE
        animationView?.playAnimation()
        animationView?.loop(true)

        val client = retrofit.create(ApiService::class.java)
        val call: Call<Pic>
        call = client.getPictures(requireActivity().resources.getString(R.string.mercedes), index)
        call.enqueue(object : Callback<Pic> {
            override fun onResponse(call: Call<Pic>, response: Response<Pic>) {
                animationView?.loop(false)
                animationView?.visibility = View.GONE
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
                animationView?.loop(false)
                animationView?.visibility = View.GONE
                Toast.makeText(context, context?.resources?.getString(R.string.wrong_message),
                        Toast.LENGTH_SHORT).show()
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

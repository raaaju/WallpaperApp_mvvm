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
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_car_brand.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class CarBrandFragment : Fragment() {

    companion object {
        const val FETCH_TYPE = "fetch_type"
    }

    @Inject
    lateinit var retrofit: Retrofit

    lateinit var adapter: WallpAdapter
    private var picResult: Pic? = Pic()
    private var type: String? = null
    private var columnNo: Int = 0

    override fun onAttach(context: Context) {
        (MyApplication.instance as MyApplication)
                .getApiComponent()
                .inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_car_brand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!UtilityMethods.isNetworkAvailable) {
            Toast.makeText(requireContext(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show()
        }

        type = arguments?.getString(FETCH_TYPE)

        loadData(1)
        brandRecyclerView.setHasFixedSize(true)

        checkScreenSize()

        val gridLayoutManager = StaggeredGridLayoutManager(columnNo, StaggeredGridLayoutManager.VERTICAL)
        brandRecyclerView.layoutManager = gridLayoutManager

        val listener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        brandRecyclerView.addOnScrollListener(listener)
        adapter = WallpAdapter(requireContext())
        brandRecyclerView.adapter = adapter

        brandRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && requireActivity().navigation.isShown) {
                    requireActivity().navigation?.visibility = View.GONE
                } else if (dy < 0) {
                    requireActivity().navigation?.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun loadData(index: Int) {
        brandAnimationView?.visibility = View.VISIBLE
        brandAnimationView?.playAnimation()
        brandAnimationView?.loop(true)

        val client = retrofit.create(ApiService::class.java)
        val call: Call<Pic>
        call = client.getPictures(type ?: "", index)
        call.enqueue(object : Callback<Pic> {
            override fun onResponse(call: Call<Pic>, response: Response<Pic>) {
                brandAnimationView?.loop(false)
                brandAnimationView?.visibility = View.GONE
                try {
                    if (!response.isSuccessful) {
                        Log.d(resources.getString(R.string.No_Success),
                                response.errorBody()?.string())
                    } else {
                        picResult = response.body()
                        if (picResult != null) {
                            adapter.setPicList(picResult?.hits as MutableList<Hit>)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Pic>, t: Throwable) {
                brandAnimationView?.loop(false)
                brandAnimationView?.visibility = View.GONE
                Toast.makeText(requireContext(), resources
                        .getString(R.string.wrong_message), Toast.LENGTH_SHORT).show()
            }
        })
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
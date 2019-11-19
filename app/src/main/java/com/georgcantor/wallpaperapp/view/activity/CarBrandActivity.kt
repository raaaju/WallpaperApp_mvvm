package com.georgcantor.wallpaperapp.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.DisposableManager
import com.georgcantor.wallpaperapp.util.EndlessRecyclerViewScrollListener
import com.georgcantor.wallpaperapp.util.getScreenSize
import com.georgcantor.wallpaperapp.util.hideAnimation
import com.georgcantor.wallpaperapp.util.longToast
import com.georgcantor.wallpaperapp.util.showAnimation
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.view.fragment.BmwFragment.Companion.REQUEST
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_car_brand.animationView
import kotlinx.android.synthetic.main.activity_car_brand.brandRecyclerView
import kotlinx.android.synthetic.main.activity_car_brand.brandTitle
import kotlinx.android.synthetic.main.activity_car_brand.brandToolbar
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CarBrandActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: PicturesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_brand)
        setSupportActionBar(brandToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        brandTitle.text = intent.getStringExtra(REQUEST)

        viewModel = getViewModel { parametersOf() }
        setupRecyclerView()

        loadData(1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = StaggeredGridLayoutManager(
            getScreenSize(),
            StaggeredGridLayoutManager.VERTICAL
        )
        brandRecyclerView.layoutManager = gridLayoutManager

        adapter = PicturesAdapter(this)
        brandRecyclerView.adapter = adapter

        val scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        brandRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun loadData(index: Int) {
        val disposable =
            viewModel.getPics(intent.getStringExtra(REQUEST) ?: "", index)
                .doOnSubscribe {
                    animationView?.showAnimation()
                }
                .doFinally {
                    animationView?.hideAnimation()
                    try {
                        viewModel.noInternetShow.observe(this, Observer {
                            if (it) longToast(getString(R.string.no_internet))
                        })
                    } catch (e: IllegalStateException) {
                    }
                }
                .subscribe(adapter::setPicList) {
                }

        DisposableManager.add(disposable)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}
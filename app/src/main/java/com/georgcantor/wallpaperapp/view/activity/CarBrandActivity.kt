package com.georgcantor.wallpaperapp.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.REQUEST
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_car_brand.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CarBrandActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: PicturesAdapter
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_brand)
        setSupportActionBar(brandToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!this.isNetworkAvailable()) {
            noInternetImageView.visible()
            this.longToast(getString(R.string.no_internet))
        }

        brandTitle.text = intent.getStringExtra(REQUEST)

        viewModel = getViewModel { parametersOf() }
        setupRecyclerView()

        refreshLayout.setOnRefreshListener {
            loadData(1)
            refreshLayout.isRefreshing = false
        }

        loadData(1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_gallery).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_search -> openActivity(SearchActivity::class.java)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = StaggeredGridLayoutManager(
            getScreenSize(),
            StaggeredGridLayoutManager.VERTICAL
        )
        brandRecyclerView.layoutManager = gridLayoutManager

        adapter = PicturesAdapter(this)
        brandRecyclerView.adapter = adapter

        val scrollListener = object : EndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        brandRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun loadData(index: Int) {
        disposable.add(
            viewModel.getPics(intent.getStringExtra(REQUEST) ?: "", index)
                .doOnSubscribe {
                    animationView?.showAnimation()
                }
                .doFinally {
                    animationView?.hideAnimation()
                }
                .subscribe(adapter::setPictures) {
                    viewModel.getPixabayPictures(intent.getStringExtra(REQUEST) ?: "", index)
                        .subscribe(adapter::setPictures) {
                            viewModel.getPics(intent.getStringExtra(REQUEST) ?: "", index)
                                .subscribe(adapter::setPictures) {}
                        }
                }
        )
    }
}
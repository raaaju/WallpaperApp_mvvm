package com.georgcantor.wallpaperapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_results.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 111
        private const val PERMISSION_REQUEST_CODE = 222
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var manager: InputMethodManager
    lateinit var adapter: PicturesAdapter
    private var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_search)

        viewModel = getViewModel { parametersOf() }
        createToolbar()
        initViews()

        searchEditText.requestFocus()
        searchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, 0)
                adapter.clearPicList()
                search(searchEditText.text.toString().trim { it <= ' ' }, index)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun createToolbar() {
        setSupportActionBar(toolbarSearch)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        toolbarSearch.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        toolbarSearch.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }
    }

    private fun initViews() {
        searchRecyclerView.setHasFixedSize(true)

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(
            UtilityMethods.getScreenSize(this),
            StaggeredGridLayoutManager.VERTICAL
        )
        searchRecyclerView.layoutManager = staggeredGridLayoutManager

        val listener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                search(searchEditText.text.toString().trim { it <= ' ' }, page)
            }
        }
        searchRecyclerView.addOnScrollListener(listener)
        adapter = PicturesAdapter(this)
        searchRecyclerView.adapter = adapter
    }

    fun search(search: String, index: Int) {
        swipeRefreshLayoutSearch.isEnabled = true
        swipeRefreshLayoutSearch.isRefreshing = true

        val disposable = viewModel.searchPics(search, index)
            .subscribe({
                adapter.setPicList(it)
                searchAnimationView?.hideAnimation()
                invalidateOptionsMenu()
                if (it.isNullOrEmpty()) {
                    searchAnimationView?.showAnimation()
                    shortToast(getString(R.string.not_found))
                }
                swipeRefreshLayoutSearch.isRefreshing = false
                swipeRefreshLayoutSearch.isEnabled = false
            }, {
                searchAnimationView?.showAnimation()
                swipeRefreshLayoutSearch.isRefreshing = false
                swipeRefreshLayoutSearch.isEnabled = false
                shortToast(getString(R.string.something_went_wrong))
            })
        DisposableManager.add(disposable)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val cancel = menu?.findItem(R.id.action_cancel)
        cancel?.isVisible = viewModel.isSearchingActive.value == true

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cancel -> {
                viewModel.isSearchingActive.value = false
                searchEditText.setText("")
                manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
            R.id.action_voice_search -> checkPermission()
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
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
            shortToast(getString(R.string.something_went_wrong))
        }
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speak()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            arrayList?.toString()?.let { search(it, index) }
            searchEditText.setText(arrayList?.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } catch (e: UninitializedPropertyAccessException) {
        }
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}

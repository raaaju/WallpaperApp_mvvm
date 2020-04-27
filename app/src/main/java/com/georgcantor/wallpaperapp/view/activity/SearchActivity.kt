package com.georgcantor.wallpaperapp.view.activity

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
import android.view.View
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
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_results.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 111
        private const val PERMISSION_REQUEST_CODE = 222
        private const val HISTORY = "history"
    }

    private val disposable = CompositeDisposable()
    private var index = 1

    private lateinit var viewModel: SearchViewModel
    private lateinit var manager: InputMethodManager
    private lateinit var adapter: PicturesAdapter
    private lateinit var prefManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_search)

        if (!this.isNetworkAvailable()) this.longToast(getString(R.string.no_internet))

        manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        prefManager = PreferenceManager(this)

        viewModel = getViewModel { parametersOf() }
        createToolbar()
        initViews()

        searchView.requestFocus()
        searchView.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                adapter.clearPictures()
                search(searchView.text.toString().trim { it <= ' ' }, index)
                return@OnEditorActionListener true
            }
            false
        })

        historyTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_history, 0, 0, 0)

        prefManager.getString(HISTORY)?.let { request ->
            historyTextView.visibility = if (request == "") View.GONE else View.VISIBLE
            historyTextView.text = request

            historyTextView.setOnClickListener {
                adapter.clearPictures()
                search(request, 1)
                hideKeyboard()
                historyTextView.gone()
                searchView.setText(request)
                searchView.setSelection(searchView.text.length)
                searchView.setAdapter(null)
            }
        }
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
                searchAnimationView?.hideAnimation()
                viewModel.isSearchingActive.value = false
                searchView.setText("")
                manager.showSoftInput(searchView, 0)
            }
            R.id.action_voice_search -> checkPermission()
        }
        return super.onOptionsItemSelected(item)
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
            searchView.setText(arrayList?.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideKeyboard()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onDestroy() {
        prefManager.saveString(HISTORY, searchView.text.toString().trim { it <= ' ' })
        disposable.dispose()
        super.onDestroy()
    }

    private fun createToolbar() {
        setSupportActionBar(toolbarSearch)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        toolbarSearch.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        toolbarSearch.setNavigationOnClickListener {
            super.onBackPressed()
            hideKeyboard()
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }
    }

    private fun initViews() {
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(
            getScreenSize(),
            StaggeredGridLayoutManager.VERTICAL
        )
        searchRecyclerView.layoutManager = staggeredGridLayoutManager
        searchRecyclerView.setHasFixedSize(true)
        adapter = PicturesAdapter(this)
        searchRecyclerView.adapter = adapter

        val listener = object : EndlessScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                search(searchView.text.toString().trim { it <= ' ' }, page)
            }
        }
        searchRecyclerView.addOnScrollListener(listener)
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
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun search(search: String, index: Int) {
        disposable.add(
            viewModel.searchPics(search, index)
                .doOnSubscribe {
                    swipeRefreshLayoutSearch.isEnabled = true
                    swipeRefreshLayoutSearch.isRefreshing = true
                }
                .doFinally {
                    swipeRefreshLayoutSearch.isRefreshing = false
                    swipeRefreshLayoutSearch.isEnabled = false
                }
                .subscribe({
                    adapter.setPictures(it)
                    searchAnimationView?.hideAnimation()
                    invalidateOptionsMenu()
                    if (adapter.itemCount == 0) {
                        searchAnimationView?.showAnimation()
                        shortToast(getString(R.string.not_found))
                    }
                }, {
                    searchAnimationView?.showAnimation()
                    shortToast(getString(R.string.something_went_wrong))
                })
        )
    }

    private fun hideKeyboard() {
        manager.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }
}

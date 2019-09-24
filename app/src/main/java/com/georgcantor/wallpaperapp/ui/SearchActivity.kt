package com.georgcantor.wallpaperapp.ui

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.adapter.SearchAdapter
import com.georgcantor.wallpaperapp.ui.util.*
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
    private var columnNo: Int = 0
    lateinit var adapter: SearchAdapter
    private var index = 1
    private var voiceInvisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_search)

        viewModel = getViewModel { parametersOf() }
        createToolbar()
        initViews()

        searchEditText.requestFocus()
        searchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, 0)
                searchEverything(searchEditText.text.toString().trim { it <= ' ' }, index)
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
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
        adapter = SearchAdapter(this)
        searchRecyclerView.adapter = adapter
    }

    @SuppressLint("CheckResult")
    fun searchEverything(search: String, index: Int) {
        swipeRefreshLayoutSearch.isEnabled = true
        swipeRefreshLayoutSearch.isRefreshing = true

        val disposable = viewModel.searchPics(search, index)
            .subscribe({
                adapter.setPicList(it.hits)
                searchAnimationView?.hideAnimation()
                invalidateOptionsMenu()
                voiceInvisible = true
                searchEditText.visibility = View.GONE
                if (it.hits.isNullOrEmpty()) {
                    searchAnimationView?.showAnimation()
                    shortToast(getString(R.string.not_found))
                }
                swipeRefreshLayoutSearch.isRefreshing = false
                swipeRefreshLayoutSearch.isEnabled = false
            }, {
                searchAnimationView?.showAnimation()
                swipeRefreshLayoutSearch.isRefreshing = false
                swipeRefreshLayoutSearch.isEnabled = false
                shortToast(it.message.toString())
            })
        DisposableManager.add(disposable)
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
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.setNeutralButton(getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            arrayList?.toString()?.let { searchEverything(it, index) }
            searchEditText.setText(arrayList?.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}

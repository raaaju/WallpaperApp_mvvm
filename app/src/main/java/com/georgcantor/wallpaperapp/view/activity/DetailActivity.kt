package com.georgcantor.wallpaperapp.view.activity

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.AndroidRuntimeException
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.EXTRA_PIC
import com.georgcantor.wallpaperapp.util.Constants.FULL_EXTRA
import com.georgcantor.wallpaperapp.util.Constants.IS_PORTRAIT
import com.georgcantor.wallpaperapp.util.Constants.PREF_BOOLEAN
import com.georgcantor.wallpaperapp.view.adapter.SimilarAdapter
import com.georgcantor.wallpaperapp.viewmodel.DetailsViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {

    private var pic: CommonPic? = null
    private var permissionCheck: Int = 0
    private val disposable = CompositeDisposable()

    private lateinit var prefManager: PreferenceManager
    private lateinit var viewModel: DetailsViewModel
    private lateinit var zoomyBuilder: Zoomy.Builder

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabClock: Animation
    private lateinit var fabAnticlock: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_detail)

        viewModel = getViewModel { parametersOf(this) }
        prefManager = PreferenceManager(this)

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        fabClock = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_clock)
        fabAnticlock = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_anticlock)

        progress_anim?.showAnimation()

        if (!isNetworkAvailable()) longToast(getString(R.string.no_internet))

        initView()

        pic?.url?.let {
            viewModel.picInFavorites(it)
                .subscribe({ isFav ->
                    if (isFav) bottom_app_bar.menu.findItem(R.id.action_add_to_fav).setIcon(R.drawable.ic_star_red_24dp)
                }, {
                })
        }

        pic?.tags.let {
            viewModel.getSimilarImages(it ?: "")
        }

        viewModel.isProgressVisible.observe(this, androidx.lifecycle.Observer {
            when (it) {
                true -> similar_progress.showAnimation()
                false -> similar_progress.hideAnimation()
            }
        })

        viewModel.pictures.observe(this, androidx.lifecycle.Observer {
            similar_recycler.adapter = SimilarAdapter(it) {
                this.openActivity(DetailActivity::class.java) { putParcelable(EXTRA_PIC, it) }
            }
        })

        zoomyBuilder = Zoomy.Builder(this)
            .target(image)
            .doubleTapListener {
                pic?.let {
                    viewModel.setFavoriteStatus(
                        it,
                        bottom_app_bar.menu.findItem(R.id.action_add_to_fav),
                        star_anim,
                        unstar_anim
                    )
                }
            }
        zoomyBuilder.register()

        viewModel.isFabOpened.observe(this, androidx.lifecycle.Observer { open ->
            fab.setOnClickListener {
                if (!open) {
                    fab_full.visible()
                    fab_set_wall.visible()
                    fab_full.isClickable = true
                    fab_set_wall.isClickable = true
                    fab.startAnimation(fabClock)
                    fab_set_wall.startAnimation(fabOpen)
                    fab_full.startAnimation(fabOpen)
                    viewModel.setFabState(true)
                } else {
                    fab_full.gone()
                    fab_set_wall.gone()
                    fab_full.isClickable = false
                    fab_set_wall.isClickable = false
                    fab.startAnimation(fabAnticlock)
                    fab_set_wall.startAnimation(fabClose)
                    fab_full.startAnimation(fabClose)
                    viewModel.setFabState(false)
                }
            }
        })

        fab_set_wall.setOnClickListener {
            if (this.isNetworkAvailable()) {
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    progress_anim?.showAnimation()
                    setWallAsync()
                } else {
                    prefManager.saveBoolean(PREF_BOOLEAN, true)
                    viewModel.checkSavingPermission(permissionCheck, this)
                }
            } else {
                longToast(getString(R.string.no_internet))
            }
        }

        fab_full.setOnClickListener {
            val intent = Intent(this, FullScreenActivity::class.java)
            intent.putExtra(FULL_EXTRA, pic?.imageURL)
            intent.putExtra(IS_PORTRAIT, pic?.heght ?: 0 > pic?.width ?: 0)
            startActivity(intent)
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        bottom_app_bar.setNavigationOnClickListener { onBackPressed() }

        bottom_app_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_share -> share()
                R.id.action_download -> startDownloading()
                R.id.action_add_to_fav -> {
                    pic?.let { pic ->
                        viewModel.setFavoriteStatus(pic, it, star_anim, unstar_anim)
                    }
                }
            }
            true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            ORIENTATION_LANDSCAPE -> {
                similar_text.visibility = GONE
                similar_recycler.visibility = GONE
            }
            ORIENTATION_PORTRAIT -> {
                similar_text.visibility = VISIBLE
                similar_recycler.visibility = VISIBLE
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (prefManager.getBoolean(PREF_BOOLEAN)) {
                true -> {
                    prefManager.saveBoolean(PREF_BOOLEAN, false)
                    setWallAsync()
                }
                false -> pic?.let { pic?.let { saveImage(it.imageURL ?: "") } }
            }
        } else {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            finish()
            longToast(getString(R.string.you_need_perm_toast))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
        }
        Zoomy.unregister(image)
        disposable.dispose()
        super.onDestroy()
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            shortToast(getString(R.string.down_complete))
            try {
                progress_anim?.hideAnimation()
            } catch (e: IndexOutOfBoundsException) {
                progress_anim?.hideAnimation()
            }
        }
    }

    private fun initView() {
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (intent.hasExtra(EXTRA_PIC)) {
            pic = intent.getParcelableExtra(EXTRA_PIC)
        } else {
            shortToast(getString(R.string.something_went_wrong))
        }

        pic?.let { pic ->
            pic.imageURL?.let {
                loadImage(
                    it,
                    resources.getDrawable(R.drawable.placeholder),
                    image,
                    progress_anim
                )
            }
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, filter)
    }

    private fun setWallAsync() {
        progress_anim?.showAnimation()

        pic?.let { pic ->
            disposable.add(
                viewModel.getBitmapAsync(pic)
                    .subscribe({
                        val wallpaperManager = WallpaperManager.getInstance(baseContext)
                        it?.let { bitmap ->
                            viewModel.getImageUri(bitmap)
                                .subscribe({ uri ->
                                    try {
                                        startActivity(Intent(wallpaperManager.getCropAndSetWallpaperIntent(uri)))
                                    } catch (e: IllegalArgumentException) {
                                        try {
                                            it.let { bitMap ->
                                                viewModel.getImageUri(bitMap)
                                                    .subscribe({ uri ->
                                                        val bitmap2 =
                                                            MediaStore.Images.Media.getBitmap(
                                                                contentResolver,
                                                                uri
                                                            )
                                                        viewModel.setBitmapAsync(bitmap2)
                                                    }, {
                                                        shortToast(getString(R.string.something_went_wrong))
                                                    })
                                            }
                                        } catch (e: OutOfMemoryError) {
                                            shortToast(getString(R.string.something_went_wrong))
                                        }
                                    }
                                }, { throwable ->
                                    longToast(throwable.message.toString())
                                })
                        }
                        longToast(getString(R.string.wallpaper_is_install))
                        recreate()
                    }, {
                        shortToast(getString(R.string.something_went_wrong))
                    })
            )
        }
    }

    private fun share() {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, pic?.imageURL)
            startActivity(Intent.createChooser(intent, getString(R.string.choose_share)))
        } catch (e: AndroidRuntimeException) {
            shortToast(getString(R.string.cant_share))
        }
    }

    private fun startDownloading() {
        if (!this.isNetworkAvailable()) {
            shortToast(getString(R.string.no_internet))
            return
        }
        progress_anim.showAnimation()

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            pic?.let { saveImage(it.imageURL ?: "") }
        } else {
            viewModel.checkSavingPermission(permissionCheck, this)
        }

        shortToast(getString(R.string.download_start))
        Handler().postDelayed({
            shortToast(getString(R.string.down_complete))
            progress_anim.hideAnimation()
        }, 5000)
    }
}

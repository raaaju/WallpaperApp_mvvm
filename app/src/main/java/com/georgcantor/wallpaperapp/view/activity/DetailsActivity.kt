package com.georgcantor.wallpaperapp.view.activity

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.AndroidRuntimeException
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.DatabaseHelper
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.view.activity.FullScreenActivity.Companion.FULL_EXTRA
import com.georgcantor.wallpaperapp.view.activity.FullScreenActivity.Companion.IS_PORTRAIT
import com.georgcantor.wallpaperapp.view.adapter.SimilarAdapter
import com.georgcantor.wallpaperapp.view.adapter.TagAdapter
import com.georgcantor.wallpaperapp.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.util.*

class DetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PIC = "picture"
        const val PREF_BOOLEAN = "is_set_wall"
        const val MY_PREFS = "my_prefs"
    }

    private var pic: CommonPic? = null
    private var first = 0
    private var file: File? = null
    private var tagTitle: TextView? = null
    private var permissionCheck: Int = 0
    private var db: DatabaseHelper? = null
    private val tags = ArrayList<String>()

    private lateinit var prefManager: PreferenceManager
    private lateinit var tagAdapter: TagAdapter
    private lateinit var similarAdapter: SimilarAdapter
    private lateinit var viewModel: DetailsViewModel
    private lateinit var zoomyBuilder: Zoomy.Builder
    private lateinit var menu: Menu

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
        db = DatabaseHelper(this)

        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        fabClock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_clock)
        fabAnticlock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_anticlock)

        progressAnimationView?.showAnimation()

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))
        if (!isNetworkAvailable()) longToast(getString(R.string.no_internet))

        initView()

        zoomyBuilder = Zoomy.Builder(this)
                .target(detailImageView)
                .doubleTapListener {
                    pic?.let {
                        viewModel.setFavoriteStatus(it, menu.findItem(R.id.action_add_to_fav), starAnimationView, unstarAnimationView)
                    }
                }
        zoomyBuilder.register()

        viewModel.isFabOpened.observe(this, androidx.lifecycle.Observer { open ->
            fab.setOnClickListener {
                if (!open) {
                    fabFull.visible()
                    fabSetWall.visible()
                    fabFull.isClickable = true
                    fabSetWall.isClickable = true
                    fab.startAnimation(fabClock)
                    fabSetWall.startAnimation(fabOpen)
                    fabFull.startAnimation(fabOpen)
                    viewModel.setFabSate(true)
                } else {
                    fabFull.gone()
                    fabSetWall.gone()
                    fabFull.isClickable = false
                    fabSetWall.isClickable = false
                    fab.startAnimation(fabAnticlock)
                    fabSetWall.startAnimation(fabClose)
                    fabFull.startAnimation(fabClose)
                    viewModel.setFabSate(false)
                }
            }
        })

        fabSetWall.setOnClickListener {
            if (this.isNetworkAvailable()) {
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    progressAnimationView?.showAnimation()
                    setWallAsync()
                } else {
                    prefManager.saveBoolean(PREF_BOOLEAN, true)
                    viewModel.checkSavingPermission(permissionCheck, this)
                }
            } else {
                longToast(getString(R.string.no_internet))
            }
        }

        fabFull.setOnClickListener {
            val intent = Intent(this, FullScreenActivity::class.java)
            intent.putExtra(FULL_EXTRA, pic?.imageURL)
            intent.putExtra(IS_PORTRAIT, pic?.heght ?: 0 > pic?.width ?: 0)
            startActivity(intent)
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fav, menu)
        this.menu = menu
        val starItem = menu.findItem(R.id.action_add_to_fav)
        db?.let {
            if (it.containFav(pic?.url.toString())) {
                starItem.setIcon(R.drawable.ic_star_red_24dp)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_add_to_fav -> {
                pic?.let { viewModel.setFavoriteStatus(it, item, starAnimationView, unstarAnimationView) }
            }
            R.id.action_share -> share()
            R.id.action_download -> startDownloading()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val isSetWall = prefManager.getBoolean(PREF_BOOLEAN)
            if (isSetWall) setWallAsync() else pic?.let { viewModel.downloadPicture(it, tags, downloadAnimationView) }
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
        Zoomy.unregister(detailImageView)
        super.onDestroy()
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            shortToast(getString(R.string.down_complete))
            try {
                downloadAnimationView?.hideAnimation()
            } catch (e: IndexOutOfBoundsException) {
                downloadAnimationView?.hideAnimation()
            }
        }
    }

    private fun initView() {
        permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        similarRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        similarAdapter = SimilarAdapter(this)
        similarRecyclerView.adapter = similarAdapter

        if (intent.hasExtra(EXTRA_PIC)) {
            pic = intent.getParcelableExtra(EXTRA_PIC)
        } else {
            shortToast(getString(R.string.something_went_wrong))
        }
        pic?.let {
            if (it.tags.isNullOrEmpty()) {
                tagsCardView.gone()
                similarTextView.gone()
                similarCardView.gone()
            } else {
                var title = it.tags
                while (title?.contains(",") == true) {
                    val element = title.substring(0, title.indexOf(","))
                    tags.add(element)
                    first = title.indexOf(",")
                    title = title.substring(++first)
                }
                title?.let(tags::add)

                loadSimilarImages(tags[0])
            }
        }

        tagTitle?.text = tags[0]
        tagsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        tagAdapter = TagAdapter(this)
        tagAdapter.setTagList(tags, this)
        tagsRecyclerView.adapter = tagAdapter
        file = File(
            Environment.getExternalStoragePublicDirectory(
                "/" + resources
                    .getString(R.string.app_name)
            ), pic?.id.toString() + resources.getString(R.string.jpg)
        )

        pic?.let { pic ->
            pic.imageURL?.let {
                loadImage(
                        it,
                        resources.getDrawable(R.drawable.placeholder),
                        detailImageView,
                        progressAnimationView
                )
            }

            loadCircleImage(
                    if (pic.userImageURL?.isNotEmpty() == true) pic.userImageURL ?: "" else pic.url ?: "",
                    userImageView
            )
        }
        nameTextView.text = pic?.user
        downloadsTextView.text = pic?.downloads.toString()
        favoritesTextView.text = pic?.favorites.toString()

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, filter)
    }

    private fun setWallAsync() {
        progressAnimationView?.showAnimation()

        val disposable = pic?.let { pic ->
            viewModel.getBitmapAsync(pic)
                ?.subscribe({
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
                                                    val bitmap2 = MediaStore.Images.Media.getBitmap(
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
        }
        disposable?.let(DisposableManager::add)
    }

    private fun loadSimilarImages(request: String) {
        val disposable = viewModel.getSimilarImages(request, 1)
            .retry(3)
            .doOnSubscribe {
                similarProgressAnimView?.showAnimation()
            }
            .doFinally {
                similarProgressAnimView?.hideAnimation()
            }
            .subscribe({
                similarAdapter.setList(it, this)
            }, {
                shortToast(getString(R.string.something_went_wrong))
            })

        DisposableManager.add(disposable)
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
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                pic?.imageURL?.let { viewModel.downloadPictureQ(it, downloadAnimationView) }
            } else {
                pic?.let { viewModel.downloadPicture(it, tags, downloadAnimationView) }
            }
        } else {
            viewModel.checkSavingPermission(permissionCheck, this)
        }
    }

}

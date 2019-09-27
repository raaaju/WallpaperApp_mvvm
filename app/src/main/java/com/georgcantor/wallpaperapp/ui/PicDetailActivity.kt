package com.georgcantor.wallpaperapp.ui

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.ui.adapter.TagAdapter
import com.georgcantor.wallpaperapp.ui.util.*
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_detail.detailImageView
import kotlinx.android.synthetic.main.activity_detail.downloadAnimationView
import kotlinx.android.synthetic.main.activity_detail.downloadsTextView
import kotlinx.android.synthetic.main.activity_detail.fabSetWall
import kotlinx.android.synthetic.main.activity_detail.favoritesTextView
import kotlinx.android.synthetic.main.activity_detail.nameTextView
import kotlinx.android.synthetic.main.activity_detail.progressAnimationView
import kotlinx.android.synthetic.main.activity_detail.tagsRecyclerView
import kotlinx.android.synthetic.main.activity_detail.userImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class PicDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PIC = "picture"
        const val PREF_BOOLEAN = "is_set_wall"
        const val MY_PREFS = "my_prefs"
    }

    private var hit: CommonPic? = null
    private val tags = ArrayList<String>()
    private var first = 0
    private lateinit var tagAdapter: TagAdapter
    private var file: File? = null
    private var tagTitle: TextView? = null
    private var permissionCheck: Int = 0
    private var db: DatabaseHelper? = null
    private var pathOfFile: String? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_detail)
        progressAnimationView?.showAnimation()

        db = DatabaseHelper(this)

        editor = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit()
        prefs = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))

        initView()

        fabSetWall.setOnClickListener {
            if (this.isNetworkAvailable()) {
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    val uri = Uri.fromFile(file)
                    pathOfFile = UtilityMethods.getPath(applicationContext, uri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        progressAnimationView?.showAnimation()
                        setWallAsync()
                    } else {
                        setAsWallpaper()
                    }
                } else {
                    editor.putBoolean(PREF_BOOLEAN, true)
                    editor.apply()
                    checkSavingPermission()
                }
            } else {
                longToast(getString(R.string.no_internet))
            }
        }
    }

    private fun setAsWallpaper() {
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_ATTACH_DATA)
        intent.setDataAndType(uri, getString(R.string.image_jpg))
        intent.putExtra(getString(R.string.mimeType), getString(R.string.image_jpg))

        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.Set_As)), 200
        )
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                shortToast(tags[0] + getString(R.string.down_complete))
                downloadAnimationView?.hideAnimation()
            } catch (e: IndexOutOfBoundsException) {
                downloadAnimationView?.hideAnimation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setWallAsync() {
        progressAnimationView?.showAnimation()

        val disposable = getBitmapAsync()?.subscribe({
            val wallpaperManager = WallpaperManager.getInstance(baseContext)
            it?.let { bitmap ->
                getImageUri(bitmap, applicationContext).subscribe({ uri ->
                    try {
                        startActivity(
                            Intent(
                                wallpaperManager.getCropAndSetWallpaperIntent(
                                    uri
                                )
                            )
                        )
                    } catch (e: IllegalArgumentException) {
                        try {
                            it.let { bitMap ->
                                getImageUri(bitMap, applicationContext).subscribe({ uri ->
                                    val bitmap2 = MediaStore.Images.Media.getBitmap(
                                        contentResolver,
                                        uri
                                    )
                                    WallpaperManager.getInstance(this@PicDetailActivity)
                                        .setBitmap(bitmap2)
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
            shortToast(getString(R.string.wallpaper_is_install))
            recreate()
        }, {
            longToast(getString(R.string.something_went_wrong))
        })

        disposable?.let(DisposableManager::add)
    }

    private fun getBitmapAsync(): Observable<Bitmap?>? {
        return Observable.fromCallable {
            var result: Bitmap? = null
            try {
                result = Picasso.with(applicationContext)
                    .load(hit?.imageURL)
                    .get()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            result
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getImageUri(inImage: Bitmap, inContext: Context): Observable<Uri> {
        return Observable.fromCallable {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage, "Title", null
            )
            Uri.parse(path)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun initView() {
        permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (intent.hasExtra(EXTRA_PIC)) {
            hit = intent.getParcelableExtra(EXTRA_PIC)
        } else {
            shortToast(getString(R.string.something_went_wrong))
        }
        hit?.let {
            var title = it.tags
            while (title?.contains(",") == true) {
                val element = title.substring(0, title.indexOf(","))
                tags.add(element)
                first = title.indexOf(",")
                title = title.substring(++first)
            }
            title?.let(tags::add)
        }

        tagTitle?.text = tags[0]
        tagsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        tagAdapter = TagAdapter(this)
        tagAdapter.setTagList(tags)
        tagsRecyclerView.adapter = tagAdapter
        file = File(
            Environment.getExternalStoragePublicDirectory(
                "/" + resources
                    .getString(R.string.app_name)
            ), hit?.id.toString() + resources
                .getString(R.string.jpg)
        )

        Picasso.with(this)
            .load(hit?.fullHDURL)
            .placeholder(R.drawable.plh)
            .into(detailImageView, object : Callback {
                override fun onSuccess() {
                    progressAnimationView?.hideAnimation()
                }

                override fun onError() {
                    progressAnimationView?.hideAnimation()
                    shortToast(getString(R.string.something_went_wrong))
                }
            })

        nameTextView.text = hit?.user
        downloadsTextView.text = hit?.downloads.toString()
        favoritesTextView.text = hit?.favorites.toString()
        if (!this.isNetworkAvailable()) {
            Picasso.with(this)
                .load(R.drawable.memb)
                .transform(CropCircleTransformation())
                .into(userImageView)
        } else {
            hit?.let {
                if (it.userImageURL?.isNotEmpty() == true) {
                    Picasso.with(this)
                        .load(hit?.userImageURL)
                        .transform(CropCircleTransformation())
                        .into(userImageView)
                } else {
                    Picasso.with(this)
                        .load(R.drawable.memb)
                        .transform(CropCircleTransformation())
                        .into(userImageView)
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fav, menu)
        db?.let {
            if (it.containFav(hit?.url.toString())) {
                menu.findItem(R.id.action_add_to_fav).setIcon(R.drawable.ic_star_red_24dp)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        db?.let { db ->
            when (item.itemId) {
                android.R.id.home -> onBackPressed()
                R.id.action_add_to_fav -> if (!db.containFav(hit?.url.toString())) {
                    hit?.let { addToFavorite(it.url.toString(), it.imageURL.toString(), it) }
                    item.setIcon(R.drawable.ic_star_red_24dp)
                    shortToast(getString(R.string.add_to_fav_toast))
                } else {
                    db.deleteFromFavorites(hit?.url.toString())
                    item.setIcon(R.drawable.ic_star_border)
                    shortToast(getString(R.string.del_from_fav_toast))
                }
                R.id.action_share -> try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    val sAux = hit?.url
                    intent.putExtra(Intent.EXTRA_TEXT, sAux)
                    startActivity(Intent.createChooser(intent, getString(R.string.choose_share)))
                } catch (e: AndroidRuntimeException) {
                    e.printStackTrace()
                    shortToast(getString(R.string.cant_share))
                }
                R.id.action_download -> {
                    if (this.isNetworkAvailable()) {
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                downloadPictureQ(hit?.url ?: "")
                            } else {
                                downloadPicture()
                            }
                        } else {
                            checkSavingPermission()
                        }
                    } else {
                        shortToast(getString(R.string.no_internet))
                    }
                }
                else -> {
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun downloadPicture(): Long {
        downloadAnimationView?.showAnimation()
        val uri = hit?.imageURL
        val imageUri = Uri.parse(uri)

        val downloadReference: Long
        val downloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        var name = Environment.getExternalStorageDirectory().absolutePath
        name += "/YourDirectoryName/"

        val request = DownloadManager.Request(imageUri)

        try {
            request.setTitle(tags[0] + getString(R.string.down))
            request.setDescription(getString(R.string.down_wallpapers))
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                request.setDestinationInExternalPublicDir(
                    "/" + resources
                        .getString(R.string.app_name), hit?.id.toString() + resources
                        .getString(R.string.jpg)
                )
            }
        } catch (e: IllegalStateException) {
            shortToast(getString(R.string.something_went_wrong))
        } catch (e: IndexOutOfBoundsException) {
            shortToast(getString(R.string.something_went_wrong))
        }
        downloadReference = downloadManager.enqueue(request)

        return downloadReference
    }

    private fun downloadPictureQ(url: String) {
        downloadAnimationView?.visibility = View.VISIBLE
        downloadAnimationView?.playAnimation()
        downloadAnimationView?.loop(true)

        val name = url.getImageNameFromUrl()

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val request = DownloadManager.Request(Uri.parse(url))

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)

        downloadManager?.enqueue(request)
    }

    private fun checkSavingPermission() {
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            val requestCode = 102
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val isSetWall = prefs.getBoolean(PREF_BOOLEAN, false)
            if (isSetWall) setWallAsync() else downloadPicture()
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

    private fun addToFavorite(imageUrl: String, hdUrl: String, commonPic: CommonPic) {
        val gson = Gson()
        val toStoreObject = gson.toJson(commonPic)
        db?.insertToFavorites(imageUrl, hdUrl, toStoreObject)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    public override fun onDestroy() {
        try {
            unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        DisposableManager.dispose()
        super.onDestroy()
    }

}

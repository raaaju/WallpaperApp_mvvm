package com.georgcantor.wallpaperapp.ui

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AndroidRuntimeException
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper
import com.georgcantor.wallpaperapp.network.NetworkUtilities
import com.georgcantor.wallpaperapp.ui.adapter.TagAdapter
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class PicDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PIC = "picture"
        const val ORIGIN = "caller"

        fun calculateInSampleSize(options: BitmapFactory.Options,
                                  reqWidth: Int,
                                  reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
                val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()

                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }

            return inSampleSize
        }

        private fun getMimeType(url: String): String? {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }

            return type
        }
    }

    private var hit: Hit? = null
    private val tags = ArrayList<String>()
    private var first = 0
    private lateinit var networkUtilities: NetworkUtilities
    private lateinit var recyclerView: RecyclerView
    private lateinit var tagAdapter: TagAdapter
    private var file: File? = null
    private var tagTitle: TextView? = null
    private var permissionCheck: Int = 0
    private var progressBar: ProgressBar? = null
    private var fab: FloatingActionButton? = null
    private var db: DatabaseHelper? = null
    private var isDownloaded = false
    private var isCallerCollection = false
    private var pathOfFile: String? = null

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, tags[0] + resources.getString(R.string.down_complete),
                    Toast.LENGTH_SHORT).show()
            isDownloaded = true
            progressBar?.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        networkUtilities = NetworkUtilities(this)
        setContentView(R.layout.activity_pic_detail)
        fab = findViewById(R.id.fab_download)

        progressBar = findViewById(R.id.progressBarDetail)
        progressBar?.visibility = View.VISIBLE
        db = DatabaseHelper(this)

        tagTitle = findViewById(R.id.toolbar_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()

        fab?.setOnClickListener(View.OnClickListener {
            if (!fileIsExist()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission()
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        return@OnClickListener
                    }
                }
                if (networkUtilities.isInternetConnectionPresent) {
                    val builder = AlertDialog.Builder(this@PicDetailActivity)
                    builder.setTitle(R.string.download)
                    builder.setIcon(R.drawable.ic_download)
                    builder.setMessage(R.string.choose_format)

                    builder.setPositiveButton("HD") { _, _ ->
                        progressBar?.visibility = View.VISIBLE
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.webformatURL
                                val imageUri = Uri.parse(uri)
                                downloadData(imageUri)
                                fab?.setImageDrawable(VectorDrawableCompat.create(resources,
                                        R.drawable.ic_photo, null))
                            } else {
                                Toast.makeText(this@PicDetailActivity, resources
                                        .getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                progressBar?.visibility = View.GONE
                            }
                        }
                    }

                    builder.setNeutralButton(hit?.imageWidth.toString() + " x "
                            + hit?.imageHeight) { _, _ ->
                        progressBar?.visibility = View.VISIBLE
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.imageURL
                                val imageUri = Uri.parse(uri)
                                downloadData(imageUri)
                                fab?.setImageDrawable(applicationContext.resources
                                        .getDrawable(R.drawable.ic_photo))
                            } else {
                                Toast.makeText(this@PicDetailActivity, resources
                                        .getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                progressBar?.visibility = View.GONE
                            }
                        }
                    }

                    builder.setNegativeButton("FullHD") { _, _ ->
                        progressBar?.visibility = View.VISIBLE
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.fullHDURL
                                val imageUri = Uri.parse(uri)
                                downloadData(imageUri)
                                fab?.setImageDrawable(applicationContext.resources
                                        .getDrawable(R.drawable.ic_photo))
                            } else {
                                Toast.makeText(this@PicDetailActivity, resources
                                        .getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                progressBar?.visibility = View.GONE
                            }
                        }
                    }
                    builder.create().show()
                } else {
                    Toast.makeText(this@PicDetailActivity, resources
                            .getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                }

            } else {
                checkWallpPermission()
                val uri = Uri.fromFile(file)
                pathOfFile = UtilityMethods.getPath(applicationContext, uri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setAsWallpaper6(pathOfFile)
                } else {
                    setAsWallpaper()
                }
            }
        })
    }

    private fun setAsWallpaper() {
//        try {
//            val intent = Intent()
//            intent.action = Intent.ACTION_ATTACH_DATA
//            val file = File(pathOfFile)
//
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
//            intent.setDataAndType(FileProvider.getUriForFile(applicationContext,
//                    BuildConfig.APPLICATION_ID + ".provider", file), pathOfFile?.let { getMimeType(it) })
//            startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(applicationContext, "Exception generated", Toast.LENGTH_SHORT).show()
//        }
        val uri = Uri.fromFile(file)
        Log.d(resources.getString(R.string.URI), uri.toString())
        val intent = Intent(Intent.ACTION_ATTACH_DATA)
        intent.setDataAndType(uri, resources.getString(R.string.image_jpg))
        intent.putExtra(resources.getString(R.string.mimeType),
                resources.getString(R.string.image_jpg))
        startActivityForResult(Intent.createChooser(intent,
                resources.getString(R.string.Set_As)), 200)
    }

    private fun setAsWallpaper6(pathOfFile: String?) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels shl 1

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathOfFile, options)

        options.inSampleSize = calculateInSampleSize(options, width, height)

        options.inJustDecodeBounds = false
        val decodedSampleBitmap = BitmapFactory.decodeFile(pathOfFile, options)

        val wm = WallpaperManager.getInstance(this)
        try {
            wm.setBitmap(decodedSampleBitmap)
            Toast.makeText(this@PicDetailActivity,
                    getString(R.string.wallpaper_is_install), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this@PicDetailActivity,
                    getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (intent.hasExtra(EXTRA_PIC)) {
            hit = intent.getParcelableExtra(EXTRA_PIC)
        } else {
            throw IllegalArgumentException("Detail activity must receive a Hit parcelable")
        }
        hit?.let {
            var title = it.tags
            while (title.contains(",")) {
                val element = title.substring(0, title.indexOf(","))
                tags.add(element)
                first = title.indexOf(",")
                title = title.substring(++first)
            }
            tags.add(title)
        }

        tagTitle?.text = tags[0]
        val wallpaper = findViewById<ImageView>(R.id.wallpaper_detail)
        val fav = findViewById<TextView>(R.id.fav)
        val userId = findViewById<TextView>(R.id.user_name)
        val userImage = findViewById<ImageView>(R.id.user_image)
        val downloads = findViewById<TextView>(R.id.down)
        recyclerView = findViewById(R.id.tagsRv)
        recyclerView.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        tagAdapter = TagAdapter(this)
        tagAdapter.setTagList(tags)
        recyclerView.adapter = tagAdapter
        file = File(Environment.getExternalStoragePublicDirectory("/" + resources
                .getString(R.string.app_name)), hit?.id.toString() + resources
                .getString(R.string.jpg))
        if (fileIsExist()) {
            fab?.setImageDrawable(VectorDrawableCompat.create(resources,
                    R.drawable.ic_photo, null))
        }

        if (intent.hasExtra(ORIGIN)) {
            Picasso.with(this)
                    .load(file)
                    .placeholder(R.drawable.plh)
                    .into(wallpaper)
            isCallerCollection = true
        } else {
            Picasso.with(this)
                    .load(hit?.webformatURL)
                    .placeholder(R.drawable.plh)
                    .into(wallpaper, object : Callback {
                        override fun onSuccess() {
                            progressBar?.visibility = View.GONE
                        }

                        override fun onError() {
                            progressBar?.visibility = View.GONE
                            Toast.makeText(this@PicDetailActivity,
                                    getString(R.string.something_went_wrong),
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
        }

        userId.text = hit?.user
        downloads.text = hit?.downloads.toString()
        fav.text = hit?.favorites.toString()
        if (!networkUtilities.isInternetConnectionPresent) {
            Picasso.with(this)
                    .load(R.drawable.memb)
                    .transform(CropCircleTransformation())
                    .into(userImage)
        } else {
            hit?.let {
                if (it.userImageURL.isNotEmpty()) {
                    Picasso.with(this)
                            .load(hit?.userImageURL)
                            .transform(CropCircleTransformation())
                            .into(userImage)
                } else {
                    Picasso.with(this)
                            .load(R.drawable.memb)
                            .transform(CropCircleTransformation())
                            .into(userImage)
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fav, menu)
        db?.let {
            if (it.containFav(hit?.previewURL.toString())) {
                menu.findItem(R.id.action_add_to_fav).setIcon(R.drawable.ic_star_red_24dp)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        db?.let { db ->
            when (item.itemId) {
                android.R.id.home -> onBackPressed()
                R.id.action_add_to_fav -> if (!db.containFav(hit?.previewURL.toString())) {
                    addToFavorite(hit?.previewURL.toString(), hit?.pageURL.toString())
                    item.setIcon(R.drawable.ic_star_red_24dp)
                    Toast.makeText(this, R.string.add_to_fav_toast, Toast.LENGTH_SHORT).show()
                } else {
                    db.deleteFromFavorites(hit?.previewURL.toString())
                    item.setIcon(R.drawable.ic_star_border_black_24dp)
                    Toast.makeText(this, R.string.del_from_fav_toast, Toast.LENGTH_SHORT).show()
                }
                R.id.action_share -> try {
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "text/plain"
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    val sAux = hit?.imageURL
                    i.putExtra(Intent.EXTRA_TEXT, sAux)
                    startActivity(Intent.createChooser(i, getString(R.string.choose_share)))
                } catch (e: AndroidRuntimeException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Can not share image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun downloadData(uri: Uri): Long {
        val downloadReference: Long
        val downloadManager =
                getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        var name = Environment.getExternalStorageDirectory().absolutePath
        name += "/YourDirectoryName/"

        val request = DownloadManager.Request(uri)

        try {
            request.setTitle(tags[0] + resources.getString(R.string.down))
            request.setDescription(resources.getString(R.string.down_wallpapers))
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                request.setDestinationInExternalPublicDir("/" + resources
                        .getString(R.string.app_name), hit?.id.toString() + resources
                        .getString(R.string.jpg))
            }
        } catch (e: IllegalStateException) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
        }
        downloadReference = downloadManager.enqueue(request)

        return downloadReference
    }

    public override fun onDestroy() {
        try {
            unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun fileIsExist(): Boolean {
        return file?.exists() ?: false
    }

    private fun checkPermission() {
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            val requestCode = 102
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission
                    .WRITE_EXTERNAL_STORAGE), requestCode)
        }
    }

    private fun checkWallpPermission() {
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission
                    .SET_WALLPAPER), 103)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            restartActivity()
        } else {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            finish()
            Toast.makeText(this, R.string.you_need_perm_toast, Toast.LENGTH_LONG).show()
        }
    }

    private fun restartActivity() {
        hit = intent.getParcelableExtra(EXTRA_PIC)
        val intent = Intent(this@PicDetailActivity, PicDetailActivity::class.java)
        intent.putExtra(EXTRA_PIC, hit)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun addToFavorite(imageUrl: String, hdUrl: String) {
        db?.insertToFavorites(imageUrl, hdUrl)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}

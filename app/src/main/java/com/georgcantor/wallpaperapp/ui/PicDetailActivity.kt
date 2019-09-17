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
import android.util.AndroidRuntimeException
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.ui.adapter.TagAdapter
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_detail.*
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class PicDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PIC = "picture"
        const val ORIGIN = "caller"
    }

    private var hit: Hit? = null
    private val tags = ArrayList<String>()
    private var first = 0
    private lateinit var tagAdapter: TagAdapter
    private var file: File? = null
    private var tagTitle: TextView? = null
    private var permissionCheck: Int = 0
    private var db: DatabaseHelper? = null
    private var isDownloaded = false
    private var isCallerCollection = false
    private var pathOfFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.fragment_detail)

        progressAnimationView?.visibility = View.VISIBLE
        progressAnimationView?.playAnimation()
        progressAnimationView?.loop(true)
        db = DatabaseHelper(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()

        fabDownload.setOnClickListener(View.OnClickListener {
            if (!fileIsExist()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission()
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        return@OnClickListener
                    }
                }
                if (UtilityMethods.isNetworkAvailable) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.download)
                    builder.setIcon(R.drawable.ic_download)
                    builder.setMessage(R.string.choose_format)

                    builder.setPositiveButton(resources.getText(R.string.hd)) { _, _ ->
                        downloadAnimationView?.visibility = View.VISIBLE
                        downloadAnimationView?.playAnimation()
                        downloadAnimationView?.loop(true)
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.webformatURL
                                val imageUri = Uri.parse(uri)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                    downloadDataQ(hit?.webformatURL ?: "")
                                } else {
                                    downloadData(imageUri)
                                }
                                fabDownload.setImageDrawable(VectorDrawableCompat.create(resources,
                                        R.drawable.ic_photo, null))
                            } else {
                                Toast.makeText(this, resources.getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                downloadAnimationView?.loop(false)
                                downloadAnimationView?.visibility = View.GONE
                            }
                        }
                    }

                    builder.setNeutralButton(hit?.imageWidth.toString() + " x "
                            + hit?.imageHeight) { _, _ ->
                        downloadAnimationView?.visibility = View.VISIBLE
                        downloadAnimationView?.playAnimation()
                        downloadAnimationView?.loop(true)
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.imageURL
                                val imageUri = Uri.parse(uri)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                    downloadDataQ(hit?.imageURL ?: "")
                                } else {
                                    downloadData(imageUri)
                                }
                                fabDownload.setImageDrawable(ContextCompat
                                        .getDrawable(this, R.drawable.ic_photo))
                            } else {
                                Toast.makeText(this@PicDetailActivity, resources
                                        .getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                downloadAnimationView?.loop(false)
                                downloadAnimationView?.visibility = View.GONE
                            }
                        }
                    }

                    builder.setNegativeButton(resources.getText(R.string.fullHd)) { _, _ ->
                        downloadAnimationView?.visibility = View.VISIBLE
                        downloadAnimationView?.playAnimation()
                        downloadAnimationView?.loop(true)
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.fullHDURL
                                val imageUri = Uri.parse(uri)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                    downloadDataQ(hit?.fullHDURL ?: "")
                                } else {
                                    downloadData(imageUri)
                                }
                                fabDownload.setImageDrawable(ContextCompat
                                        .getDrawable(this, R.drawable.ic_photo))
                            } else {
                                Toast.makeText(this, resources.getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                downloadAnimationView?.loop(false)
                                downloadAnimationView?.visibility = View.GONE
                            }
                        }
                    }
                    builder.create().show()
                } else {
                    Toast.makeText(this, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
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

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, tags[0] + resources.getString(R.string.down_complete),
                    Toast.LENGTH_SHORT).show()
            isDownloaded = true
            downloadAnimationView?.loop(false)
            downloadAnimationView?.visibility = View.GONE
        }
    }

    private fun setAsWallpaper() {
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_ATTACH_DATA)
        intent.setDataAndType(uri, resources.getString(R.string.image_jpg))
        intent.putExtra(resources.getString(R.string.mimeType), resources.getString(R.string.image_jpg))

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

        val wallpaperManager = WallpaperManager.getInstance(this)
        try {
            wallpaperManager.setBitmap(decodedSampleBitmap)
            Toast.makeText(this, getString(R.string.wallpaper_is_install), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options,
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
        tagsRecyclerView.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        tagAdapter = TagAdapter(this)
        tagAdapter.setTagList(tags)
        tagsRecyclerView.adapter = tagAdapter
        file = File(Environment.getExternalStoragePublicDirectory("/" + resources
                .getString(R.string.app_name)), hit?.id.toString() + resources
                .getString(R.string.jpg))
        if (fileIsExist()) {
            fabDownload.setImageDrawable(VectorDrawableCompat.create(resources,
                    R.drawable.ic_photo, null))
        }

        if (intent.hasExtra(ORIGIN)) {
            Picasso.with(this)
                    .load(file)
                    .placeholder(R.drawable.plh)
                    .into(detailImageView)
            isCallerCollection = true
        } else {
            Picasso.with(this)
                    .load(hit?.webformatURL)
                    .placeholder(R.drawable.plh)
                    .into(detailImageView, object : Callback {
                        override fun onSuccess() {
                            progressAnimationView?.loop(false)
                            progressAnimationView?.visibility = View.GONE
                        }

                        override fun onError() {
                            progressAnimationView?.loop(false)
                            progressAnimationView?.visibility = View.GONE
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(hit?.pageURL)))
                            finish()
                        }
                    })
        }

        nameTextView.text = hit?.user
        downloadsTextView.text = hit?.downloads.toString()
        favoritesTextView.text = hit?.favorites.toString()
        if (!UtilityMethods.isNetworkAvailable) {
            Picasso.with(this)
                    .load(R.drawable.memb)
                    .transform(CropCircleTransformation())
                    .into(userImageView)
        } else {
            hit?.let {
                if (it.userImageURL.isNotEmpty()) {
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
                    hit?.let { addToFavorite(it.previewURL.toString(), it.pageURL.toString(), it) }
                    item.setIcon(R.drawable.ic_star_red_24dp)
                    Toast.makeText(this, R.string.add_to_fav_toast, Toast.LENGTH_SHORT).show()
                } else {
                    db.deleteFromFavorites(hit?.previewURL.toString())
                    item.setIcon(R.drawable.ic_star_border_black_24dp)
                    Toast.makeText(this, R.string.del_from_fav_toast, Toast.LENGTH_SHORT).show()
                }
                R.id.action_share -> try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    val sAux = hit?.imageURL
                    intent.putExtra(Intent.EXTRA_TEXT, sAux)
                    startActivity(Intent.createChooser(intent, getString(R.string.choose_share)))
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

    private fun downloadDataQ(url: String) {
        val name = UtilityMethods.getImageNameFromUrl(url)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val request = DownloadManager.Request(Uri.parse(url))

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)

        downloadManager?.enqueue(request)
    }


    public override fun onDestroy() {
        try {
            unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun fileIsExist(): Boolean = file?.exists() ?: false

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
            this.recreate()
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

    private fun addToFavorite(imageUrl: String, hdUrl: String, hit: Hit) {
        val gson = Gson()
        val toStoreObject = gson.toJson(hit)
        db?.insertToFavorites(imageUrl, hdUrl, toStoreObject)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

}

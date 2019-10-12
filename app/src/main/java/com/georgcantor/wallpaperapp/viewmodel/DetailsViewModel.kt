package com.georgcantor.wallpaperapp.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.AndroidRuntimeException
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.util.getImageNameFromUrl
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.util.showAnimation
import com.georgcantor.wallpaperapp.util.showSingleAnimation
import com.google.android.gms.common.util.IOUtils
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InterruptedIOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class DetailsViewModel(
    private val context: Context,
    private val db: DatabaseHelper
) : ViewModel() {

    fun setFavoriteStatus(
            pic: CommonPic,
            menuItem: MenuItem,
            starAnimation: LottieAnimationView,
            unStarAnimation: LottieAnimationView
    ) {
        if (db.containFav(pic.url.toString())) {
            db.deleteFromFavorites(pic.url.toString())
            menuItem.setIcon(R.drawable.ic_star_border)
            unStarAnimation.showSingleAnimation()
        } else {
            addToFavorites(pic.url.toString(), pic.imageURL.toString(), pic)
            menuItem.setIcon(R.drawable.ic_star_red_24dp)
            starAnimation.showSingleAnimation()
        }
    }

    private fun addToFavorites(imageUrl: String, hdUrl: String, commonPic: CommonPic) {
        val gson = Gson()
        val toStoreObject = gson.toJson(commonPic)
        db.insertToFavorites(imageUrl, hdUrl, toStoreObject)
    }

    @SuppressLint("CheckResult")
    fun doubleClickDetect(
            view: View,
            pic: CommonPic,
            menuItem: MenuItem,
            starAnimation: LottieAnimationView,
            unStarAnimation: LottieAnimationView
    ) {
        val publishSubject = PublishSubject.create<Int>()
        publishSubject
            .buffer(publishSubject.debounce(200, TimeUnit.MILLISECONDS))
            .filter { list -> list.size > 1 }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setFavoriteStatus(pic, menuItem, starAnimation, unStarAnimation)
            }
        view.setOnClickListener {
            publishSubject.onNext(0)
        }
    }

    fun imageSize(pic: CommonPic): Observable<Int> {
        return Observable.fromCallable {
            val url = URL(pic.fullHDURL)
            var size = 0
            try {
                size = IOUtils.toByteArray(url.openStream()).size
            } catch (e: InterruptedIOException) {
            }
            return@fromCallable size
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun share(url: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(
                    context,
                    Intent.createChooser(intent, context.getString(R.string.choose_share)),
                    null
                )
        } catch (e: AndroidRuntimeException) {
        }
    }

    fun downloadPicture(
            pic: CommonPic,
            tags: ArrayList<String>,
            animationView: LottieAnimationView
    ): Long {
        animationView.showAnimation()
        val uri = pic.imageURL
        val imageUri = Uri.parse(uri)
        val downloadReference: Long
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var name = Environment.getExternalStorageDirectory().absolutePath
        name += "/YourDirectoryName/"

        val request = DownloadManager.Request(imageUri)
        try {
            request.setTitle(tags[0] + context.getString(R.string.down))
            request.setDescription(context.getString(R.string.down_wallpapers))
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                request.setDestinationInExternalPublicDir(
                        "/" + context.resources
                                .getString(R.string.app_name), pic.id.toString() + context.resources
                        .getString(R.string.jpg)
                )
            }
        } catch (e: IllegalStateException) {
        } catch (e: IndexOutOfBoundsException) {
        }
        downloadReference = downloadManager.enqueue(request)

        return downloadReference
    }

    fun downloadPictureQ(url: String, animationView: LottieAnimationView) {
        animationView.showAnimation()
        val name = url.getImageNameFromUrl()
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?

        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)

        downloadManager?.enqueue(request)
    }

    fun getBitmapAsync(pic: CommonPic): Observable<Bitmap?>? {
        return Observable.fromCallable {
            var result: Bitmap? = null
            try {
                result = Picasso.with(context.applicationContext)
                        .load(pic.imageURL)
                        .get()
            } catch (e: IOException) {
            }
            result
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getImageUri(bitmap: Bitmap): Observable<Uri> {
        return Observable.fromCallable {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap, "Title", null
            )
            Uri.parse(path)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun setBitmapAsync(bitmap: Bitmap, activity: Activity) {
        Single.fromCallable {
            WallpaperManager.getInstance(context)
                .setBitmap(bitmap)
        }
            .doOnSuccess {
                activity.runOnUiThread {
                    context.shortToast(context.getString(R.string.set_wall_complete))
                }
            }
            .onErrorReturn {
                activity.runOnUiThread {
                    context.shortToast(context.getString(R.string.something_went_wrong))
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun checkSavingPermission(permissionCheck: Int, activity: Activity) {
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            val requestCode = 102
            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode
            )
        }
    }

}
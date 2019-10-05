package com.georgcantor.wallpaperapp.viewmodel

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.AndroidRuntimeException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.ui.util.getImageNameFromUrl
import com.georgcantor.wallpaperapp.ui.util.shortToast
import com.georgcantor.wallpaperapp.ui.util.showAnimation
import com.google.android.gms.common.util.IOUtils
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.URL
import java.util.*

class DetailsViewModel(
    private val context: Context,
    private val db: DatabaseHelper
) : ViewModel() {

    val isImageFavorite = MutableLiveData<Boolean>()

    fun setFavoriteStatus(pic: CommonPic) {
        if (db.containFav(pic.url.toString())) {
            db.deleteFromFavorites(pic.url.toString())
            context.shortToast(context.getString(R.string.del_from_fav_toast))
            isImageFavorite.value = false
        } else {
            addToFavorites(pic.url.toString(), pic.imageURL.toString(), pic)
            context.shortToast(context.getString(R.string.add_to_fav_toast))
            isImageFavorite.value = true
        }
    }

    private fun addToFavorites(imageUrl: String, hdUrl: String, commonPic: CommonPic) {
        val gson = Gson()
        val toStoreObject = gson.toJson(commonPic)
        db.insertToFavorites(imageUrl, hdUrl, toStoreObject)
    }

    fun imageSize(pic: CommonPic): Observable<Int> {
        return Observable.fromCallable {
            val url = URL(pic.fullHDURL)
            return@fromCallable IOUtils.toByteArray(url.openStream()).size
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
            context.shortToast(context.getString(R.string.cant_share))
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
            context.shortToast(context.getString(R.string.something_went_wrong))
        } catch (e: IndexOutOfBoundsException) {
            context.shortToast(context.getString(R.string.something_went_wrong))
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
                context.shortToast(context.getString(R.string.something_went_wrong))
            }
            result
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
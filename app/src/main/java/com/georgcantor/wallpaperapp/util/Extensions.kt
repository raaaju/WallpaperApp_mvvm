package com.georgcantor.wallpaperapp.util

import android.animation.Animator
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.georgcantor.wallpaperapp.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun AppCompatActivity.openFragment(fragment: Fragment, tag: String) {
    val transaction = supportFragmentManager.beginTransaction()
    val lastIndex = supportFragmentManager.fragments.lastIndex
    val current = supportFragmentManager.fragments[lastIndex]

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) transaction.setCustomAnimations(
            R.anim.pull_in_right,
            R.anim.push_out_left,
            R.anim.pull_in_left,
            R.anim.push_out_right
    )

    when {
        fragment == current -> return
        fragment.isAdded -> {
            transaction.replace(R.id.frame_container, fragment)
            transaction.addToBackStack(tag)
            transaction.commit()
        }
        else -> {
            transaction.add(R.id.frame_container, fragment)
            transaction.addToBackStack(tag)
            transaction.commit()
        }
    }
}

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val context = this as AppCompatActivity
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
    context.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
}

fun Context.getScreenSize(): Int {
    return when (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
        Configuration.SCREENLAYOUT_SIZE_XLARGE -> 4
        Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> 3
        Configuration.SCREENLAYOUT_SIZE_LARGE -> 3
        Configuration.SCREENLAYOUT_SIZE_NORMAL -> 2
        Configuration.SCREENLAYOUT_SIZE_SMALL -> 2
        else -> 2
    }
}

fun View.visible() { visibility = View.VISIBLE }

fun View.gone() { visibility = View.GONE }

fun LottieAnimationView.showAnimation() {
    this.visibility = View.VISIBLE
    this.playAnimation()
    this.loop(true)
}

fun LottieAnimationView.showSingleAnimation(speed: Float) {
    val animation = this
    this.visibility = View.VISIBLE
    this.playAnimation()
    this.repeatCount = 0
    this.speed = speed
    this.addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            animation.gone()
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }
    })
}

fun LottieAnimationView.hideAnimation() {
    this.loop(false)
    this.gone()
}

fun Context.shortToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.isNetworkAvailable(): Boolean {
    val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    manager?.let {
        val networkInfo = it.activeNetworkInfo
        networkInfo?.let { info ->
            if (info.isConnected) return true
        }
    }

    return false
}


fun Context.showDialog(
        message: CharSequence,
        function: () -> (Unit)
) {
    AlertDialog.Builder(this)
            .setMessage(message)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ -> function() }
            .create()
            .show()
}

fun Context.loadImage(
        url: String,
        view: ImageView,
        animView: LottieAnimationView?
) {
    Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .thumbnail(0.1F)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                ): Boolean {
                    animView?.hideAnimation()
                    return false
                }

                override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                ): Boolean {
                    animView?.hideAnimation()
                    return false
                }
            })
            .into(view)
}

fun Context.loadCircleImage(url: String, view: ImageView) {
    Glide.with(this)
            .load(url)
            .placeholder(R.drawable.memb)
            .apply(RequestOptions.circleCropTransform())
            .into(view)
}

fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

private fun Bitmap.saveImage(context: Context) {
    val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
    val myDir = File("$root/Wallpapers")
    myDir.mkdirs()
    val randomInt = (0..10000).random()
    val fileName = "Image-$randomInt.jpg"
    val file = File(myDir, fileName)
    if (file.exists()) file.delete()
    try {
        val outputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { path, uri ->
        Log.i("ExternalStorage", "Scanned $path:")
        Log.i("ExternalStorage", "-> uri=$uri")
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.saveImageQ(context: Context) {
    val values = contentValues()
    values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Wallpapers")
    values.put(MediaStore.Images.Media.IS_PENDING, true)

    val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        saveImageToStream(this, context.contentResolver.openOutputStream(uri))
        values.put(MediaStore.Images.Media.IS_PENDING, false)
        context.contentResolver.update(uri, values, null, null)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun contentValues(): ContentValues {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

    return values
}

private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
    if (outputStream != null) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Context.saveImage(url: String) {
    Glide.with(this)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            bitmap.saveImageQ(this@saveImage)
                        } else {
                            bitmap.saveImage(this@saveImage)
                        }
                    }
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}

fun Context.share(text: String?) {
    val intent = Intent().apply {
        type = "text/plain"
        putExtra(EXTRA_TEXT, text)
        putExtra(EXTRA_SUBJECT, getString(R.string.app_name))
    }
    try {
        startActivity(createChooser(intent, getString(R.string.choose_share)))
    } catch (e: ActivityNotFoundException) {
        shortToast(getString(R.string.cant_share))
    }
}
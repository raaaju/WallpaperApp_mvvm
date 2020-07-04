package com.georgcantor.wallpaperapp.util

import android.animation.Animator
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.Intent.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection.scanFile
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.Constants.BLACK
import com.georgcantor.wallpaperapp.util.Constants.BLUE
import com.georgcantor.wallpaperapp.util.Constants.GRAY
import com.georgcantor.wallpaperapp.util.Constants.GREEN
import com.georgcantor.wallpaperapp.util.Constants.RED
import com.georgcantor.wallpaperapp.util.Constants.THEME_PREF
import com.georgcantor.wallpaperapp.util.Constants.YELLOW
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_change_theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

val toasts = mutableListOf<Toast>()

fun Context.shortToast(message: String) {
    val toast = Toast.makeText(this, message, LENGTH_SHORT)
    toast.show()
    toasts.add(toast)
}

fun Context.longToast(message: String) {
    val toast = Toast.makeText(this, message, LENGTH_LONG)
    toast.show()
    toasts.add(toast)
}

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

fun Context.showThemeDialog(function: () -> (Unit)) {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_theme, null)
    val builder = AlertDialog.Builder(this).setView(dialogView)
    val alertDialog = builder.show()

    with(alertDialog) {
        window?.setBackgroundDrawable(ColorDrawable(TRANSPARENT))

        radio_group.setOnCheckedChangeListener { _, id ->
            PreferenceManager(context).saveString(
                THEME_PREF,
                when (id) {
                    R.id.radio_black -> BLACK
                    R.id.radio_blue -> BLUE
                    R.id.radio_gray -> GRAY
                    R.id.radio_red -> RED
                    R.id.radio_yellow -> YELLOW
                    R.id.radio_green -> GREEN
                    else -> ""
                }
            )
        }

        cancel_btn.setOnClickListener { dismiss() }
        ok_btn.setOnClickListener { function() }
    }
}

fun Context.isNetworkAvailable() = (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?)
    ?.activeNetworkInfo?.isConnectedOrConnecting ?: false

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

fun View.visible() { visibility = View.VISIBLE }

fun View.gone() { visibility = View.GONE }

fun LottieAnimationView.showAnimation() {
    this.visibility = View.VISIBLE
    this.playAnimation()
    this.loop(true)
}

fun LottieAnimationView.showSeveralAnimation(speed: Float, count: Int) {
    val animation = this
    this.visibility = View.VISIBLE
    this.playAnimation()
    this.repeatCount = count
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

fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return this
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

private fun Bitmap.saveImage(context: Context) {
    val root = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString()
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
    scanFile(context, arrayOf(file.toString()), null) { path, uri ->
        Log.i("ExternalStorage", "Scanned $path:")
        Log.i("ExternalStorage", "-> uri=$uri")
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.saveImageQ(context: Context) {
    val values = contentValues()
    values.put(RELATIVE_PATH, "Pictures/" + "Wallpapers")
    values.put(IS_PENDING, true)

    val uri: Uri? = context.contentResolver.insert(EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        saveImageToStream(this, context.contentResolver.openOutputStream(uri))
        values.put(IS_PENDING, false)
        context.contentResolver.update(uri, values, null, null)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun contentValues(): ContentValues {
    val values = ContentValues()
    values.put(MIME_TYPE, "image/png")
    values.put(DATE_ADDED, System.currentTimeMillis() / 1000)
    values.put(DATE_TAKEN, System.currentTimeMillis())

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
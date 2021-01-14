package com.georgcantor.wallpaperapp.util

import android.app.Activity
import android.content.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.os.Handler
import android.os.Looper.getMainLooper
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.*
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.concurrent.TimeUnit

inline fun <reified T : Activity> Activity.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
}

fun Activity.getImageUri(pic: CommonPic?): Uri {
    val bitmap = getBitmap(pic)
    val bytes = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        contentResolver, bitmap, "Title", null
    )
    return Uri.parse(path)
}

fun Activity.getBitmap(pic: CommonPic?): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        bitmap = Glide.with(this)
            .asBitmap()
            .load(pic?.imageURL)
            .submit()
            .get()
    } catch (e: IOException) {
    }
    return bitmap
}

fun Context.isNetworkAvailable() = (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?)
    ?.activeNetworkInfo?.isConnectedOrConnecting ?: false

fun Context.loadImage(url: String, view: ImageView, progressBar: ProgressBar?, color: Int) =
    Glide.with(this).load(url)
        .placeholder(color)
        .thumbnail(0.1F)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        })
        .into(view)

fun Context.share(text: String?) {
    val intent = Intent().apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    }
    try {
        startActivity(Intent.createChooser(intent, getString(R.string.choose_share)))
    } catch (e: ActivityNotFoundException) {
        shortToast(getString(R.string.cant_share))
    }
}

fun Context.shortToast(message: String) = makeText(this, message, LENGTH_SHORT).show()

fun Context.saveImage(url: String) = Glide.with(this)
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

private fun Bitmap.saveImage(context: Context) {
    val root = getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString()
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
    }
    MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { _, _ ->
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.saveImageQ(context: Context) {
    val values = ContentValues().apply {
        put(MIME_TYPE, "image/png")
        put(DATE_ADDED, System.currentTimeMillis() / 1000)
        put(DATE_TAKEN, System.currentTimeMillis())
        put(RELATIVE_PATH, "Pictures/" + "Wallpapers")
        put(IS_PENDING, true)
    }

    val uri: Uri? = context.contentResolver.insert(EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        saveImageToStream(context.contentResolver.openOutputStream(uri))
        values.put(IS_PENDING, false)
        context.contentResolver.update(uri, values, null, null)
    }
}

private fun Bitmap.saveImageToStream(outputStream: OutputStream?) {
    outputStream?.let {
        try {
            compress(Bitmap.CompressFormat.PNG, 100, it)
            it.close()
        } catch (e: Exception) {
        }
    }
}

fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

fun View.visible() { visibility = VISIBLE }

fun View.gone() { visibility = GONE }

fun Long.runDelayed(action: () -> Unit) = Handler(getMainLooper())
    .postDelayed(action, TimeUnit.MILLISECONDS.toMillis(this))

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) { bindingInflater.invoke(layoutInflater) }

fun SharedPreferences.putAny(key: String, any: Any) {
    when (any) {
        is String -> edit().putString(key, any).apply()
        is Int -> edit().putInt(key, any).apply()
    }
}

fun SharedPreferences.getAny(type: Any, key: String): Any {
    return when (type) {
        is String -> getString(key, "") as Any
        else -> getInt(key, 0)
    }
}
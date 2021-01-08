package com.georgcantor.wallpaperapp.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.georgcantor.wallpaperapp.R
import java.util.concurrent.TimeUnit

inline fun <reified T : Activity> Activity.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
}

fun Context.isNetworkAvailable() = (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?)
    ?.activeNetworkInfo?.isConnectedOrConnecting ?: false

fun Context.loadImage(url: String?, view: ImageView, progressBar: ProgressBar?, color: Int) =
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
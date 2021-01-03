package com.georgcantor.wallpaperapp.util

import android.app.Activity
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import java.util.concurrent.TimeUnit

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

fun Context.isNetworkAvailable() = (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?)
    ?.activeNetworkInfo?.isConnectedOrConnecting ?: false

fun Context.loadImage(url: String?, view: ImageView) = Glide.with(this)
    .load(url)
    .placeholder(android.R.color.black)
    .thumbnail(0.1F)
    .into(view)

fun Context.shortToast(message: String) = makeText(this, message, LENGTH_SHORT).show()

fun View.visible() { visibility = View.VISIBLE }

fun View.gone() { visibility = View.GONE }

fun Long.runDelayed(action: () -> Unit) = Handler(getMainLooper())
    .postDelayed(action, TimeUnit.MILLISECONDS.toMillis(this))

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) { bindingInflater.invoke(layoutInflater) }
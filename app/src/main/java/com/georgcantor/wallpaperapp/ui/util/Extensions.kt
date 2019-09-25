package com.georgcantor.wallpaperapp.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.georgcantor.wallpaperapp.R

fun LottieAnimationView.showAnimation() {
    this.visibility = View.VISIBLE
    this.playAnimation()
    this.loop(true)
}

fun LottieAnimationView.hideAnimation() {
    this.loop(false)
    this.visibility = View.GONE
}

fun String.getImageNameFromUrl(): String {
    val index = this.lastIndexOf("/")

    return this.substring(index)
}

fun Context.shortToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    connectivityManager?.let {
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
    val builder = AlertDialog.Builder(this)
        .setMessage(message)
        .setNegativeButton(this.resources.getString(R.string.no)) { _, _ ->
        }
        .setPositiveButton(this.resources.getString(R.string.yes)) { _, _ ->
            function()
        }

    val dialog: AlertDialog = builder.create()
    dialog.show()
}
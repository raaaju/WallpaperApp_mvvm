package com.georgcantor.wallpaperapp.ui.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView

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
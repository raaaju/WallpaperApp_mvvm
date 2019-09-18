package com.georgcantor.wallpaperapp.ui.util

import android.view.View
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
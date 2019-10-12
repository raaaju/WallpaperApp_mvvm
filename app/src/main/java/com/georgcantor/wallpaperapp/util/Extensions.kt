package com.georgcantor.wallpaperapp.util

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.fragment.CarBrandFragment
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

fun AppCompatActivity.openFragment(fragment: Fragment, tag: String) {
    val brandFragment = CarBrandFragment()
    val transaction = supportFragmentManager.beginTransaction()
    if (fragment == brandFragment) transaction.remove(fragment)

    val lastIndex = supportFragmentManager.fragments.lastIndex
    val current = supportFragmentManager.fragments[lastIndex]

    if (fragment == current && fragment != brandFragment) {
        return
    } else {
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(tag)
        transaction.commit()
    }
}

fun LottieAnimationView.showAnimation() {
    this.visibility = View.VISIBLE
    this.playAnimation()
    this.loop(true)
}

fun LottieAnimationView.showSingleAnimation() {
    val animation = this
    this.visibility = View.VISIBLE
    this.playAnimation()
    this.repeatCount = 0
    this.addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            animation.visibility = View.GONE
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }
    })
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

@SuppressLint("CheckResult")
fun Context.loadImage(
    url: String,
    drawable: Drawable,
    view: ImageView,
    animView: LottieAnimationView
) {

    Glide.with(this)
        .load(url)
        .placeholder(drawable)
        .thumbnail(0.1f)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                animView.hideAnimation()
                shortToast(getString(R.string.something_went_wrong))
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                animView.hideAnimation()
                return false
            }
        })
        .into(view)
}

fun Context.loadCircleImage(url: String, view: ImageView) {
    Picasso.with(this)
            .load(url)
            .transform(CropCircleTransformation())
            .placeholder(R.drawable.memb)
            .into(view)
}
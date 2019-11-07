package com.georgcantor.wallpaperapp.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class HideNavScrollListener(private val view: View) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0 && view.isShown) {
            view.visibility = View.GONE
        } else if (dy < 0) {
            view.visibility = View.VISIBLE
        }
    }

}
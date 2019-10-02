package com.georgcantor.wallpaperapp.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.fragment.CarBrandFragment

class MainViewModel : ViewModel() {

    private val brandFragment = CarBrandFragment()

    fun openFragment(fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        val transaction = fragmentManager.beginTransaction()
        if (fragment == brandFragment) transaction.remove(fragment)

        val lastIndex = fragmentManager.fragments.lastIndex
        val current = fragmentManager.fragments[lastIndex]

        if (fragment == current && fragment != brandFragment) {
            return
        } else {
            transaction.replace(R.id.frame_container, fragment)
            transaction.addToBackStack(tag)
            transaction.commit()
        }
    }

}
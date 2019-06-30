package com.georgcantor.wallpaperapp.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.georgcantor.wallpaperapp.R
import kotlinx.android.synthetic.main.license_layout.*

class LicenseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.license_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        license.movementMethod = LinkMovementMethod.getInstance()
        license2.movementMethod = LinkMovementMethod.getInstance()
    }
}
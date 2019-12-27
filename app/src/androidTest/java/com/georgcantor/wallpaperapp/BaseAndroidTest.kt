package com.georgcantor.wallpaperapp

import android.content.Context
import androidx.annotation.StyleRes
import androidx.test.core.app.ApplicationProvider

open class BaseAndroidTest {

    protected fun getContext(): Context = ApplicationProvider.getApplicationContext()

    @StyleRes
    protected fun getAppTheme(): Int = R.style.AppTheme

}
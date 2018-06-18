package com.georgcantor.wallpaperapp.ui.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.georgcantor.wallpaperapp.MyApplication;

/**
 * Created by Alex on 20.11.2017.
 */

public class UtilityMethods {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication
                .getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}

package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import android.content.Intent
import android.util.AndroidRuntimeException
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.ui.util.shortToast
import com.google.android.gms.common.util.IOUtils
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL

class DetailsViewModel(
    private val context: Context,
    private val db: DatabaseHelper
) : ViewModel() {

    val isImageFavorite = MutableLiveData<Boolean>()

    fun setFavoriteStatus(pic: CommonPic) {
        if (db.containFav(pic.url.toString())) {
            db.deleteFromFavorites(pic.url.toString())
            context.shortToast(context.getString(R.string.del_from_fav_toast))
            isImageFavorite.value = false
        } else {
            addToFavorites(pic.url.toString(), pic.imageURL.toString(), pic)
            context.shortToast(context.getString(R.string.add_to_fav_toast))
            isImageFavorite.value = true
        }
    }

    private fun addToFavorites(imageUrl: String, hdUrl: String, commonPic: CommonPic) {
        val gson = Gson()
        val toStoreObject = gson.toJson(commonPic)
        db.insertToFavorites(imageUrl, hdUrl, toStoreObject)
    }

    fun imageSize(pic: CommonPic): Observable<Int> {
        return Observable.fromCallable {
            val url = URL(pic.fullHDURL)
            return@fromCallable IOUtils.toByteArray(url.openStream()).size
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun share(url: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(
                    context,
                    Intent.createChooser(intent, context.getString(R.string.choose_share)),
                    null
                )
        } catch (e: AndroidRuntimeException) {
            context.shortToast(context.getString(R.string.cant_share))
        }
    }

}
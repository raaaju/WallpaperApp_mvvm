package com.georgcantor.wallpaperapp.viewmodel

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.local.db.Favorite
import com.georgcantor.wallpaperapp.util.hideAnimation
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class FavoriteViewModel(
        private val context: Context,
        private val db: DatabaseHelper
) : ViewModel() {

    fun getFavorites(): Observable<ArrayList<Favorite>> {
        return Observable.fromCallable {
            val db = DatabaseHelper(context)
            val list: ArrayList<Favorite> = ArrayList()
            list.addAll(db.allFavorites)
            list
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun isEmptyAnimVisible(animationView: LottieAnimationView) {
        if (db.historyCount > 0) {
            animationView.hideAnimation()
        } else {
            animationView.visibility = View.VISIBLE
            animationView.playAnimation()
        }
    }

    fun deleteAll(activity: Activity) {
        if (db.historyCount > 0) {
            db.deleteAll()
            activity.recreate()
        }
    }

}
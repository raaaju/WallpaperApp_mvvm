package com.georgcantor.wallpaperapp.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.georgcantor.wallpaperapp.model.local.FavDao
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.util.hideAnimation
import com.georgcantor.wallpaperapp.util.visible
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FavoriteViewModel(
        private val activity: Activity,
        private val dao: FavDao
) : ViewModel() {

    fun getFavorites(): Observable<ArrayList<Favorite>> {
        return Observable.fromCallable {
            dao.getAll() as ArrayList
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun isEmptyAnimVisible(animationView: LottieAnimationView) {
        Observable.fromCallable {
            if (dao.getAll().isNotEmpty()) {
                activity.runOnUiThread(animationView::hideAnimation)
            } else {
                activity.runOnUiThread {
                    animationView.visible()
                    animationView.playAnimation()
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun deleteAll() {
        Observable.fromCallable {
            if (dao.getAll().isNotEmpty()) {
                dao.deleteAll()
                activity.runOnUiThread(activity::recreate)
            }
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun dbIsNotEmpty(): Observable<Boolean> {
        return Observable.fromCallable {
            dao.getAll().isNotEmpty()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}
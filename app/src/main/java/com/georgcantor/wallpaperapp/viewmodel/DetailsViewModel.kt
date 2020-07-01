package com.georgcantor.wallpaperapp.viewmodel

import android.app.Activity
import android.app.Application
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.MenuItem
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.FavDao
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.applySchedulers
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.util.showSeveralAnimation
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException

class DetailsViewModel(
    app: Application,
    private val activity: Activity,
    private val dao: FavDao,
    private val apiRepository: ApiRepository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()
    private val disposable = CompositeDisposable()

    val isFabOpened = MutableLiveData<Boolean>().apply { postValue(false) }
    val pictures = MutableLiveData<MutableList<CommonPic>>()
    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }

    fun setFabState(isOpened: Boolean) {
        isFabOpened.value = isOpened
    }

    fun setFavoriteStatus(
        pic: CommonPic,
        menuItem: MenuItem,
        starAnimation: LottieAnimationView,
        unStarAnimation: LottieAnimationView
    ) {
        pic.url?.let {
            Observable.fromCallable {
                if (dao.getByUrl(it).isNotEmpty()) {
                    dao.deleteByUrl(it)
                    activity.runOnUiThread {
                        menuItem.setIcon(R.drawable.ic_star_border)
                        unStarAnimation.showSeveralAnimation(1.5F, 0)
                    }
                } else {
                    addToFavorites(pic)
                    activity.runOnUiThread {
                        menuItem.setIcon(R.drawable.ic_star_red_24dp)
                        starAnimation.showSeveralAnimation(1F, 0)
                    }
                }
            }
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

    fun getSimilarImages(request: String) {
        disposable.add(
            Observable.fromCallable {
                apiRepository.getPixabayPictures(request, 1)
                    .doFinally { isProgressVisible.postValue(false) }
                    .subscribe(pictures::postValue) {}
            }
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    fun getBitmapAsync(pic: CommonPic): Observable<Bitmap?> {
        return Observable.fromCallable {
            var result: Bitmap? = null
            try {
                result = Glide.with(context)
                    .asBitmap()
                    .load(pic.imageURL)
                    .submit()
                    .get()
            } catch (e: IOException) {
            }
            result
        }
            .applySchedulers()
    }

    fun getImageUri(bitmap: Bitmap): Observable<Uri> {
        return Observable.fromCallable {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver, bitmap, "Title", null
            )
            Uri.parse(path)
        }
            .applySchedulers()
    }

    fun setBitmapAsync(bitmap: Bitmap) {
        Single.fromCallable {
            WallpaperManager.getInstance(context)
                .setBitmap(bitmap)
        }
            .doOnSuccess {
                activity.runOnUiThread {
                    context.shortToast(context.getString(R.string.set_wall_complete))
                }
            }
            .onErrorReturn {
                activity.runOnUiThread {
                    context.shortToast(context.getString(R.string.something_went_wrong))
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun picInFavorites(url: String): Observable<Boolean> {
        return Observable.fromCallable {
            dao.getByUrl(url).isNotEmpty()
        }
            .applySchedulers()
    }

    private fun addToFavorites(pic: CommonPic) {
        Observable.fromCallable {
            val json = Gson().toJson(pic)
            pic.url?.let {
                dao.insert(Favorite(it, json))
            }
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}
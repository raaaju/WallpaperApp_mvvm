package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import com.georgcantor.wallpaperapp.util.longToast
import com.georgcantor.wallpaperapp.view.activity.MainActivity
import com.georgcantor.wallpaperapp.view.activity.MainActivity.Companion.APP_URL
import com.georgcantor.wallpaperapp.view.activity.MainActivity.Companion.IS_RATING_EXIST
import com.georgcantor.wallpaperapp.view.activity.MainActivity.Companion.LAUNCHES
import com.georgcantor.wallpaperapp.view.fragment.CategoryFragment
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class MainViewModel(
    private val context: Context,
    private val apiRepository: ApiRepository
) : ViewModel() {

    val isGalleryVisible = MutableLiveData<Boolean>()

    fun loadCategories(preferenceManager: PreferenceManager): Observable<ArrayList<Category>?> {
        return Observable.fromCallable {
            isGalleryVisible.postValue(false)

            val categories = ArrayList<Category>()
            apiRepository.getCategories(context.getString(R.string.animals)).subscribe ({
                categories.add(Category(context.getString(R.string.Animals), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.buildings)).subscribe ({
                categories.add(Category(context.getString(R.string.Buildings), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.computer)).subscribe ({
                categories.add(Category(context.getString(R.string.Computer), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.education)).subscribe ({
                categories.add(Category(context.getString(R.string.Education), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.health)).subscribe ({
                categories.add(Category(context.getString(R.string.Health), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.fashion)).subscribe ({
                categories.add(Category(context.getString(R.string.Fashion), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.feelings)).subscribe ({
                categories.add(Category(context.getString(R.string.Feelings), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.food)).subscribe ({
                categories.add(Category(context.getString(R.string.Food), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.music)).subscribe ({
                categories.add(Category(context.getString(R.string.Music), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.nature)).subscribe ({
                categories.add(Category(context.getString(R.string.Nature), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.people)).subscribe ({
                categories.add(Category(context.getString(R.string.People), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.places)).subscribe ({
                categories.add(Category(context.getString(R.string.Places), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.science)).subscribe ({
                categories.add(Category(context.getString(R.string.Science), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.sports)).subscribe ({
                categories.add(Category(context.getString(R.string.Sports), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.textures)).subscribe ({
                categories.add(Category(context.getString(R.string.Textures), it)) }, {})
            apiRepository.getCategories(context.getString(R.string.travel)).subscribe ({
                categories.add(Category(context.getString(R.string.Travel), it)) }, {})

            if (categories.size % 2 != 0) categories.removeAt(categories.size - 1)

            preferenceManager.saveCategories(CategoryFragment.CATEGORIES, categories)
            if (context.isNetworkAvailable()) isGalleryVisible.postValue(true)
            categories
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun showRatingDialog(activity: MainActivity, prefManager: PreferenceManager) {
        val ratingDialog = AlertDialog.Builder(activity)
        val linearLayout = LinearLayout(context)
        val ratingBar = RatingBar(context)
        var userMark = 0

        val db = FirebaseFirestore.getInstance()
        val mark: MutableMap<String, Triple<String, Int, String>>
        mark = HashMap()

        val phoneInfo = (
                Build.MANUFACTURER
                        + " "
                        + Build.MODEL
                        + " "
                        + Build.VERSION.RELEASE
                        + " "
                        + Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name
                )

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(50, 20, 0, 0)
        ratingBar.layoutParams = params
        ratingBar.numStars = 5
        ratingBar.stepSize = 1F

        linearLayout.addView(ratingBar)

        ratingDialog.setTitle(context.getString(R.string.rate_us))
        ratingDialog.setView(linearLayout)

        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, ratingNumber, _ ->
                userMark = ratingNumber.toInt()
            }

        ratingDialog
            .setPositiveButton(context.getString(R.string.add_review)) { _, _ ->
                mark[MainActivity.RATING] = Triple(
                    Calendar.getInstance().time.toString(), userMark, phoneInfo
                )

                if (userMark > 0) {
                    db.collection(MainActivity.RATING)
                        .add(mark)
                    prefManager.saveBoolean(IS_RATING_EXIST, true)
                }
                if (userMark > 3) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(context, intent, null)
                } else {
                    if (userMark > 0) context.longToast(context.getString(R.string.thanks_for_feedback))
                }
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        ratingDialog.create()
        ratingDialog.show()
    }

    fun checkNumberOfLaunches(activity: MainActivity, prefManager: PreferenceManager) {
        var numberOfLaunches = prefManager.getInt(LAUNCHES)
        if (numberOfLaunches < 4) {
            numberOfLaunches++
            prefManager.saveInt(LAUNCHES, numberOfLaunches)
            if (numberOfLaunches > 3 && !prefManager.getBoolean(IS_RATING_EXIST)) {
                showRatingDialog(activity, prefManager)
            }
        }
    }

}
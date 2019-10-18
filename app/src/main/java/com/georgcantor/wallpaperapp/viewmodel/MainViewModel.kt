package com.georgcantor.wallpaperapp.viewmodel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.MainActivity
import com.georgcantor.wallpaperapp.util.longToast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class MainViewModel(private val context: Context) : ViewModel() {

    fun showRatingDialog(activity: MainActivity, editor: SharedPreferences.Editor) {
        val ratingDialog = AlertDialog.Builder(activity)
        val linearLayout = LinearLayout(context)
        val ratingBar = RatingBar(context)
        var userMark = 0

        val db = FirebaseFirestore.getInstance()
        val mark: MutableMap<String, Pair<String, Int>>
        mark = HashMap()

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
                mark[MainActivity.RATING] = Pair(Calendar.getInstance().time.toString(), userMark)
                if (userMark > 0) {
                    db.collection(MainActivity.RATING)
                        .add(mark)
                    editor.putBoolean(MainActivity.IS_RATING_EXIST, true)
                    editor.apply()
                }
                if (userMark > 3) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.APP_URL))
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

}
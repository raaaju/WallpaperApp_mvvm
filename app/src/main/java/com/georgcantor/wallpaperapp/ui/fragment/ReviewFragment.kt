package com.georgcantor.wallpaperapp.ui.fragment

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.hideAnimation
import com.georgcantor.wallpaperapp.util.longToast
import com.georgcantor.wallpaperapp.util.showAnimation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_review.*

class ReviewFragment : Fragment() {

    companion object {
        private const val APP_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.wallpaperapp"
        private const val RATING = "rating"
        private const val FONT_PATH = "fonts/Montserrat-Regular.ttf"
    }

    private var rating: Int = 0
    private lateinit var animArray: Array<LottieAnimationView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_review, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().toolbar.title = getString(R.string.rating_toolbar)
        val assetManager = requireContext().applicationContext.assets
        val typeface = Typeface.createFromAsset(assetManager, FONT_PATH)
        markTextView.typeface = typeface

        val db = FirebaseFirestore.getInstance()
        val mark: MutableMap<String, Int>
        mark = HashMap()

        animArray = arrayOf(
            rating2AnimationView,
            rating3AnimationView,
            rating4AnimationView,
            rating5AnimationView
        )

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, ratingNumber, _ ->
            rating = ratingNumber.toInt()

            addReviewButton.visibility = if (rating > 0) View.VISIBLE else View.GONE
            when (rating) {
                1 -> hideAllExceptOne(rating2AnimationView)
                2 -> hideAllExceptOne(rating2AnimationView)
                3 -> hideAllExceptOne(rating3AnimationView)
                4 -> hideAllExceptOne(rating4AnimationView)
                5 -> hideAllExceptOne(rating5AnimationView)
            }
        }

        addReviewButton.setOnClickListener {
            requireActivity().toolbar.title = getString(R.string.app_name)

            mark[RATING] = rating
            db.collection(RATING)
                .add(mark)

            if (rating > 3) {
                requireFragmentManager().popBackStack()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL)))
            } else {
                requireFragmentManager().popBackStack()
                requireActivity().longToast(getString(R.string.thanks_for_feedback))
            }
        }
    }

    private fun hideAllExceptOne(animationView: LottieAnimationView) {
        animArray.map {
            if (it != animationView) {
                it.hideAnimation()
            } else {
                it.showAnimation()
            }
        }
    }

}
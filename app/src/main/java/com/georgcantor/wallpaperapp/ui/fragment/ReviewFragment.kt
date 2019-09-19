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
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.AboutActivity.Companion.FONT_PATH
import com.georgcantor.wallpaperapp.ui.util.hideAnimation
import com.georgcantor.wallpaperapp.ui.util.longToast
import com.georgcantor.wallpaperapp.ui.util.showAnimation
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_review.*

class ReviewFragment : Fragment() {

    companion object {
        private const val APP_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.wallpaperapp"
    }

    private var rating: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_review, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().toolbar.title = "Rating"
        val assetManager = requireContext().applicationContext.assets
        val typeface = Typeface.createFromAsset(assetManager, FONT_PATH)
        markTextView.typeface = typeface

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, ratingNumber, _ ->
            rating = ratingNumber.toInt()
            addReviewButton.visibility = if (rating > 0) View.VISIBLE else View.GONE
            if (rating in 1..3) {
                cryAnimationView?.showAnimation()
            } else {
                cryAnimationView?.hideAnimation()
            }

            if (rating > 3) {
                joyAnimationView?.showAnimation()
            } else {
                joyAnimationView?.hideAnimation()
            }
        }

        addReviewButton.setOnClickListener {
            requireActivity().toolbar.title = getString(R.string.app_name)
            if (rating > 3) {
                requireFragmentManager().popBackStack()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL)))
            } else {
                requireFragmentManager().popBackStack()
                requireActivity().longToast(getString(R.string.thanks_for_feedback))
            }
        }
    }

}
package com.georgcantor.wallpaperapp.ui.fragment

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.georgcantor.wallpaperapp.R
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_review.*

class ReviewFragment : Fragment() {

    companion object {
        private const val APP_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.wallpaperapp"
    }

    private var rating: Int = 0

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().toolbar.title = "Rating"
        val assetManager = requireContext().applicationContext.assets
        val typeface = Typeface.createFromAsset(assetManager, "fonts/Montserrat-Regular.ttf")
        markTextView.typeface = typeface

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, ratingNumber, _ ->
            rating = ratingNumber.toInt()
            addReviewButton.visibility = if (rating > 0) View.VISIBLE else View.GONE
        }

        addReviewButton.setOnClickListener {
            requireActivity().toolbar.title = this.resources.getString(R.string.app_name)
            if (rating > 3) {
                requireFragmentManager().popBackStack()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL)))
            } else {
                requireFragmentManager().popBackStack()
                Toast.makeText(requireContext(), this.resources.getString(R.string.thanks_for_feedback), Toast.LENGTH_LONG).show()
            }
        }
    }
}
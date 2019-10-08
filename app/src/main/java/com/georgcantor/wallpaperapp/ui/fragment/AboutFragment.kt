package com.georgcantor.wallpaperapp.ui.fragment

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.util.shortToast
import com.georgcantor.wallpaperapp.ui.util.showAnimation
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.about_header.aboutHeaderDescription
import kotlinx.android.synthetic.main.activity_about.aboutAppBar
import kotlinx.android.synthetic.main.activity_about.aboutFab
import kotlinx.android.synthetic.main.card_info.infoCardView
import kotlinx.android.synthetic.main.card_info.infoTextView
import kotlinx.android.synthetic.main.card_music_app.playerAppCardView
import kotlinx.android.synthetic.main.card_new_app.newsAppCardView
import kotlinx.android.synthetic.main.content_about.developedByTextView
import kotlinx.android.synthetic.main.content_about.tryAppTextView
import kotlinx.android.synthetic.main.content_about.welcomeAnimationView

class AboutFragment : Fragment() {

    companion object {
        private const val DEV_URL = "https://play.google.com/store/apps/dev?id=5242637664196553916&hl=en"
        private const val NEWS_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.vipnews&hl=en"
        private const val MUSIC_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.player"
        const val FONT_PATH = "fonts/Montserrat-Regular.ttf"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_about, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomeAnimationView.showAnimation()

        aboutFab.setOnClickListener { sendEmail() }

        aboutAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                } else if (isShow) {
                    isShow = false
                }
            }
        })

        val assetManager = requireActivity().assets
        val typeface = Typeface.createFromAsset(assetManager, FONT_PATH)

        aboutHeaderDescription.typeface = typeface
        infoTextView.typeface = typeface
        developedByTextView.typeface = typeface
        tryAppTextView.typeface = typeface

        infoCardView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DEV_URL)))
        }

        newsAppCardView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NEWS_URL)))
        }

        playerAppCardView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MUSIC_URL)))
        }
    }

    private fun sendEmail() {
        val value = arrayOf(getString(R.string.email))
        val emailIntent = Intent(Intent.ACTION_SEND)

        emailIntent.data = Uri.parse("mail to: ")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, value)

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.message_choose_title)))
        } catch (ex: android.content.ActivityNotFoundException) {
            context?.shortToast(getString(R.string.no_email_client))
        }
    }

}
package com.georgcantor.wallpaperapp.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.georgcantor.wallpaperapp.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.about_header.*
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.card_info.*
import kotlinx.android.synthetic.main.card_music_app.*
import kotlinx.android.synthetic.main.card_new_app.*
import kotlinx.android.synthetic.main.content_about.*

class AboutActivity : AppCompatActivity() {

    companion object {
        private const val DEV_URL = "https://play.google.com/store/apps/dev?id=5242637664196553916&hl=en"
        private const val NEWS_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.vipnews&hl=en"
        private const val MUSIC_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.player"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(aboutToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        val assetManager = this.applicationContext.assets
        val typeface = Typeface.createFromAsset(assetManager, "fonts/Montserrat-Regular.ttf")

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sendEmail() {
        val value = arrayOf(getString(R.string.email))
        val emailIntent = Intent(Intent.ACTION_SEND)

        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, value)

        try {
            startActivity(Intent.createChooser(emailIntent, this.resources.getString(R.string.message_choose_title)))
            finish()
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, resources.getText(R.string.no_email_client), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}

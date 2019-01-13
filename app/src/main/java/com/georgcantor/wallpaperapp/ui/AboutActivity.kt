package com.georgcantor.wallpaperapp.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.georgcantor.wallpaperapp.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_layout_about)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fab = findViewById<FloatingActionButton>(R.id.fab_about)
        fab.setOnClickListener { sendEmail() }

        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_about)
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {

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
        val typeface = Typeface.createFromAsset(assetManager,
                "fonts/Montserrat-Regular.ttf")

        val appDescription = findViewById<TextView>(R.id.about_header_app_description)
        appDescription.typeface = typeface

        val cardInfo = findViewById<TextView>(R.id.tv_card_info)
        cardInfo.typeface = typeface

        val madeWithLove = findViewById<TextView>(R.id.tv_made_with_love)
        madeWithLove.typeface = typeface

        val tryMyApp = findViewById<TextView>(R.id.tv_try_my_new_app)
        tryMyApp.typeface = typeface

        val cardViewInfo = findViewById<CardView>(R.id.card_info)
        val cardViewNewApp = findViewById<CardView>(R.id.card_new_app)

        cardViewInfo.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/dev?id=5242637664196553916&hl=en"))
            startActivity(browserIntent)
        }

        cardViewNewApp.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.georgcantor.vipnews&hl=en"))
            startActivity(browserIntent)
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
        Log.i("Send email", "")
        val value = arrayOf("cupsman1986@gmail.com")
        val emailIntent = Intent(Intent.ACTION_SEND)

        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, value)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here")

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            finish()
            Log.i("Finished sending email", "")
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this@AboutActivity,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}

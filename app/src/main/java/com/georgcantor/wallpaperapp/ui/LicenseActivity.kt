package com.georgcantor.wallpaperapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.widget.TextView

import com.georgcantor.wallpaperapp.R

class LicenseActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.license_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_license)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.License)

        val textView = findViewById<TextView>(R.id.license)
        textView.movementMethod = LinkMovementMethod.getInstance()
        val textView2 = findViewById<TextView>(R.id.license2)
        textView2.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }

        return super.onOptionsItemSelected(item)
    }
}

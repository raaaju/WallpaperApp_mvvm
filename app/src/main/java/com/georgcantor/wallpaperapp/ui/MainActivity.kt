package com.georgcantor.wallpaperapp.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.georgcantor.wallpaperapp.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val FONT_PATH = "fonts/Montserrat-Regular.ttf"
    }

    private var doubleTap = false
    private lateinit var licenseFragment: Fragment
    private lateinit var brandFragment: Fragment
    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.georgcantor.wallpaperapp.R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction()
                .replace(com.georgcantor.wallpaperapp.R.id.frame_container, MercedesFragment())
                .commit()

        licenseFragment = LicenseFragment()
        brandFragment = CarBrandFragment()

        bundle = Bundle()

        val myTitle = toolbar.getChildAt(0) as TextView

        val typeface = Typeface.createFromAsset(assets, FONT_PATH)
        if (Build.VERSION.SDK_INT >= 16) {
            myTitle.typeface = Typeface.create("cursive", Typeface.NORMAL)
        } else {
            myTitle.setTypeface(typeface, Typeface.BOLD)
        }
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                com.georgcantor.wallpaperapp.R.string.navigation_drawer_open, com.georgcantor.wallpaperapp.R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()

        val itemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.georgcantor.wallpaperapp.R.id.nav_mercedes -> {
                    toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.mercedes)
                    val fragment = MercedesFragment.newInstance()
                    openFragment(fragment, "mercedes")
                    return@OnNavigationItemSelectedListener true
                }
                com.georgcantor.wallpaperapp.R.id.nav_bmw -> {
                    toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.bmw)
                    val fragment = BmwFragment.newInstance()
                    openFragment(fragment, "bmw")
                    return@OnNavigationItemSelectedListener true
                }
                com.georgcantor.wallpaperapp.R.id.nav_gallery -> {
                    toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.gallery)
                    val fragment = CategoryFragment.newInstance()
                    openFragment(fragment, "gallery")
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        navigation.setOnNavigationItemSelectedListener(itemSelectedListener)

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.replace(com.georgcantor.wallpaperapp.R.id.frame_container, fragment)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    public override fun onStart() {
        super.onStart()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            val searchItem = menu?.findItem(com.georgcantor.wallpaperapp.R.id.action_search)
            searchItem?.isVisible = false
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.georgcantor.wallpaperapp.R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.georgcantor.wallpaperapp.R.id.action_search -> {
                openSearchActivity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openSearchActivity() {
        startActivity(Intent(this, SearchActivity::class.java))
        overridePendingTransition(com.georgcantor.wallpaperapp.R.anim.pull_in_right, com.georgcantor.wallpaperapp.R.anim.push_out_left)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.georgcantor.wallpaperapp.R.id.nav_aston -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.aston)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.aston))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "aston")
            }
            com.georgcantor.wallpaperapp.R.id.nav_bentley -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.bentley)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.bentley))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "bentley")
            }
            com.georgcantor.wallpaperapp.R.id.nav_porsche -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.porsche)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.porsche))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "porsche")
            }
            com.georgcantor.wallpaperapp.R.id.nav_audi -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.audi)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.audi))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "audi")
            }
            com.georgcantor.wallpaperapp.R.id.nav_bugatti -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.bugatti)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.bugatti))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "bugatti")
            }
            com.georgcantor.wallpaperapp.R.id.nav_mclaren -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.mclaren)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.mclaren))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "mclaren")
            }
            com.georgcantor.wallpaperapp.R.id.nav_ferrari -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.ferrari)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.ferrari))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "ferrari")
            }
            com.georgcantor.wallpaperapp.R.id.nav_lambo -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.lamborghini)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(com.georgcantor.wallpaperapp.R.string.lamborghini))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "lamborghini")
            }
            com.georgcantor.wallpaperapp.R.id.nav_favorites -> {
                startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
                overridePendingTransition(com.georgcantor.wallpaperapp.R.anim.pull_in_right, com.georgcantor.wallpaperapp.R.anim.push_out_left)
            }
            com.georgcantor.wallpaperapp.R.id.nav_about -> {
                val intentAbout = Intent(this, AboutActivity::class.java)
                this.startActivity(intentAbout)
                overridePendingTransition(com.georgcantor.wallpaperapp.R.anim.pull_in_right, com.georgcantor.wallpaperapp.R.anim.push_out_left)
            }
            com.georgcantor.wallpaperapp.R.id.nav_license -> {
                toolbar.title = this.resources.getString(com.georgcantor.wallpaperapp.R.string.license)
                openFragment(licenseFragment, "license")
                overridePendingTransition(com.georgcantor.wallpaperapp.R.anim.pull_in_right, com.georgcantor.wallpaperapp.R.anim.push_out_left)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onBackPressed() {
        val stackEntryCount = supportFragmentManager.backStackEntryCount
        if (stackEntryCount == 0) {
            when {
                drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
                doubleTap -> closeApp()
                else -> {
                    Toast.makeText(this, this.resources.getString(com.georgcantor.wallpaperapp.R.string.press_back),
                            Toast.LENGTH_SHORT).show()
                    doubleTap = true
                    val handler = Handler()
                    handler.postDelayed({ doubleTap = false }, 2000)
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun closeApp() {
        finish()
    }
}

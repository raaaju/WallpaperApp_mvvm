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
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.fragment.BmwFragment
import com.georgcantor.wallpaperapp.ui.fragment.CarBrandFragment
import com.georgcantor.wallpaperapp.ui.fragment.CategoryFragment
import com.georgcantor.wallpaperapp.ui.fragment.MercedesFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val FONT_PATH = "fonts/Montserrat-Regular.ttf"
    }

    private var doubleTap = false
    private lateinit var brandFragment: Fragment
    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, MercedesFragment())
                .commit()

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
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()

        val itemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mercedes -> {
                    toolbar.title = this.resources.getString(R.string.mercedes)
                    val fragment = MercedesFragment.newInstance()
                    openFragment(fragment, "mercedes")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_bmw -> {
                    toolbar.title = this.resources.getString(R.string.bmw)
                    val fragment = BmwFragment.newInstance()
                    openFragment(fragment, "bmw")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_gallery -> {
                    toolbar.title = this.resources.getString(R.string.gallery)
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
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    public override fun onStart() {
        super.onStart()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            val searchItem = menu?.findItem(R.id.action_search)
            searchItem?.isVisible = false
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                openSearchActivity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openSearchActivity() {
        startActivity(Intent(this, SearchActivity::class.java))
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_aston -> {
                toolbar.title = this.resources.getString(R.string.aston)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.aston))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "aston")
            }
            R.id.nav_bentley -> {
                toolbar.title = this.resources.getString(R.string.bentley)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.bentley))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "bentley")
            }
            R.id.nav_porsche -> {
                toolbar.title = this.resources.getString(R.string.porsche)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.porsche))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "porsche")
            }
            R.id.nav_audi -> {
                toolbar.title = this.resources.getString(R.string.audi)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.audi))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "audi")
            }
            R.id.nav_bugatti -> {
                toolbar.title = this.resources.getString(R.string.bugatti)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.bugatti))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "bugatti")
            }
            R.id.nav_mclaren -> {
                toolbar.title = this.resources.getString(R.string.mclaren)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.mclaren))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "mclaren")
            }
            R.id.nav_ferrari -> {
                toolbar.title = this.resources.getString(R.string.ferrari)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.ferrari))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "ferrari")
            }
            R.id.nav_lambo -> {
                toolbar.title = this.resources.getString(R.string.lamborghini)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.lamborghini))
                brandFragment.arguments = bundle
                openFragment(brandFragment, "lamborghini")
            }
            R.id.nav_favorites -> {
                startActivity(Intent(this, FavoriteActivity::class.java))
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
            }
            R.id.nav_about -> {
                val intentAbout = Intent(this, AboutActivity::class.java)
                this.startActivity(intentAbout)
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onBackPressed() {
        toolbar.title = this.resources.getString(R.string.app_name)
        val stackEntryCount = supportFragmentManager.backStackEntryCount
        if (stackEntryCount == 0) {
            when {
                drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
                doubleTap -> closeApp()
                else -> {
                    Toast.makeText(this, this.resources.getString(R.string.press_back),
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

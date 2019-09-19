package com.georgcantor.wallpaperapp.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.fragment.*
import com.georgcantor.wallpaperapp.ui.util.DisposableManager
import com.georgcantor.wallpaperapp.ui.util.shortToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    InstallStateUpdatedListener {

    companion object {
        private const val APP_URL = "https://play.google.com/store/apps/details?id=com.georgcantor.wallpaperapp"
    }

    override fun onStateUpdate(installState: InstallState) {
    }

    private lateinit var updateManager: AppUpdateManager
    private lateinit var mercedesFragment: Fragment
    private lateinit var bmwFragment: Fragment
    private lateinit var categoryFragment: Fragment
    private lateinit var brandFragment: Fragment
    private lateinit var reviewFragment: Fragment

    private lateinit var bundle: Bundle
    private var doubleTap = false

    private val updateAvailable = MutableLiveData<Boolean>().apply { value = false }
    private var updateInfo: AppUpdateInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        updateManager = AppUpdateManagerFactory.create(this)
        updateManager.registerListener(this)

        checkForUpdate()

        updateAvailable.observe(this, Observer {
            if (it) showUpdateDialog()
        })

        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, MercedesFragment.newInstance(getString(R.string.mercedes)))
                .commit()

        mercedesFragment = MercedesFragment.newInstance(getString(R.string.mercedes))
        bmwFragment = BmwFragment.newInstance("bmw")
        categoryFragment = CategoryFragment.newInstance()
        brandFragment = CarBrandFragment()
        reviewFragment = ReviewFragment()

        bundle = Bundle()

        val myTitle = toolbar.getChildAt(0) as TextView
        myTitle.typeface = Typeface.create("cursive", Typeface.NORMAL)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val itemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mercedes -> {
                    toolbar.title = getString(R.string.mercedes)
                    openFragment(mercedesFragment, getString(R.string.mercedes))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_bmw -> {
                    toolbar.title = getString(R.string.bmw)
                    openFragment(bmwFragment, getString(R.string.bmw))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_gallery -> {
                    toolbar.title = getString(R.string.gallery)
                    openFragment(categoryFragment, getString(R.string.gallery))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        navigation.setOnNavigationItemSelectedListener(itemSelectedListener)
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null
    }

    private fun checkForUpdate() {
        updateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                updateInfo = it
                updateAvailable.value = true
            } else {
                updateAvailable.value = false
            }
        }
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.update_dialog_message))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL)))
            finish()
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.create().show()
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        if (fragment == brandFragment) transaction.remove(fragment)

        val lastIndex = supportFragmentManager.fragments.lastIndex
        val current = supportFragmentManager.fragments[lastIndex]

        if (fragment == current && fragment != brandFragment) {
            return
        } else {
            transaction.replace(R.id.frame_container, fragment)
            transaction.addToBackStack(tag)
            transaction.commit()
        }
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
                toolbar.title = resources.getString(R.string.aston)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.aston))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.aston))
            }
            R.id.nav_bentley -> {
                toolbar.title = resources.getString(R.string.bentley)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.bentley))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.bentley))
            }
            R.id.nav_porsche -> {
                toolbar.title = resources.getString(R.string.porsche)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.porsche))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.porsche))
            }
            R.id.nav_audi -> {
                toolbar.title = resources.getString(R.string.audi)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.audi))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.audi))
            }
            R.id.nav_bugatti -> {
                toolbar.title = resources.getString(R.string.bugatti)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.bugatti))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.bugatti))
            }
            R.id.nav_mclaren -> {
                toolbar.title = resources.getString(R.string.mclaren)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.mclaren))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.mclaren))
            }
            R.id.nav_ferrari -> {
                toolbar.title = resources.getString(R.string.ferrari)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.ferrari))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.ferrari))
            }
            R.id.nav_lambo -> {
                toolbar.title = resources.getString(R.string.lamborghini)
                bundle.putString(CarBrandFragment.FETCH_TYPE, resources.getString(R.string.lamborghini))
                brandFragment.arguments = bundle
                openFragment(brandFragment, resources.getString(R.string.lamborghini))
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
            R.id.nav_rate_us -> {
                openFragment(reviewFragment, "review")
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onBackPressed() {
        toolbar.title = resources.getString(R.string.app_name)
        val stackEntryCount = supportFragmentManager.backStackEntryCount
        if (stackEntryCount == 0) {
            when {
                drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(
                    GravityCompat.START
                )
                doubleTap -> super.onBackPressed()
                else -> {
                    this.shortToast(getString(R.string.press_back))
                    doubleTap = true
                    val handler = Handler()
                    handler.postDelayed({ doubleTap = false }, 2000)
                }
            }
        } else {
            try {
                super.onBackPressed()
            } catch (e: IllegalStateException) {
            }
        }
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
        updateManager.unregisterListener(this)
    }

}

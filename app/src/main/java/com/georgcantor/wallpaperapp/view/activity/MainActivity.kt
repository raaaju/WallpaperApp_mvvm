package com.georgcantor.wallpaperapp.view.activity

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.Companion.APP_URL
import com.georgcantor.wallpaperapp.util.Constants.Companion.REQUEST
import com.georgcantor.wallpaperapp.view.fragment.AudiFragment
import com.georgcantor.wallpaperapp.view.fragment.BmwFragment
import com.georgcantor.wallpaperapp.view.fragment.CategoryFragment
import com.georgcantor.wallpaperapp.view.fragment.MercedesFragment
import com.georgcantor.wallpaperapp.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    InstallStateUpdatedListener {

    override fun onStateUpdate(installState: InstallState) {
    }

    private val updateAvailable = MutableLiveData<Boolean>().apply { value = false }
    private val backPressedSubject = BehaviorSubject.createDefault(0L)
    private val disposable = CompositeDisposable()
    private var updateInfo: AppUpdateInfo? = null

    private lateinit var updateManager: AppUpdateManager
    private lateinit var mercedesFragment: Fragment
    private lateinit var bmwFragment: Fragment
    private lateinit var audiFragment: Fragment
    private lateinit var categoryFragment: Fragment
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val prefManager = PreferenceManager(this)
        viewModel = getViewModel { parametersOf(prefManager, this) }
        updateManager = AppUpdateManagerFactory.create(this)
        updateManager.registerListener(this)

        viewModel.loadCategories()

        checkForUpdate()

        updateAvailable.observe(this, Observer {
            if (it) showDialog(getString(R.string.update_dialog_message), ::goToGooglePlay)
        })

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, BmwFragment())
            .commit()

        mercedesFragment = MercedesFragment()
        bmwFragment = BmwFragment()
        audiFragment = AudiFragment()
        categoryFragment = CategoryFragment()

        val myTitle = toolbar.getChildAt(0) as TextView
        myTitle.typeface = Typeface.create("cursive", Typeface.NORMAL)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val itemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_bmw -> {
                    toolbar.title = getString(R.string.bmw)
                    openFragment(bmwFragment, getString(R.string.bmw))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_audi -> {
                    toolbar.title = getString(R.string.audi)
                    openFragment(audiFragment, getString(R.string.audi))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_mercedes -> {
                    toolbar.title = getString(R.string.mercedes)
                    openFragment(mercedesFragment, getString(R.string.mercedes))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        navigation.setOnNavigationItemSelectedListener(itemSelectedListener)
        navView.setNavigationItemSelectedListener(this)
        navView.itemIconTintList = null

        viewModel.checkNumberOfLaunches()

        if (viewModel.isSixDayOfMonth(Calendar.getInstance())) {
            Constants.RATING = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu.findItem(R.id.action_gallery)
        viewModel.isGalleryVisible.observe(this, Observer {
            menuItem.isVisible = it
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> openActivity(SearchActivity::class.java)
            R.id.action_gallery -> {
                toolbar.title = getString(R.string.gallery_toolbar)
                openFragment(categoryFragment, getString(R.string.gallery_toolbar))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_aston -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.aston))
                }
            }
            R.id.nav_bentley -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.bentley))
                }
            }
            R.id.nav_bugatti -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.bugatti))
                }
            }
            R.id.nav_ferrari -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.ferrari))
                }
            }
            R.id.nav_lambo -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.lamborghini))
                }
            }
            R.id.nav_mclaren -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.mclaren))
                }
            }
            R.id.nav_porsche -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.porsche))
                }
            }
            R.id.nav_rolls -> {
                openActivity(CarBrandActivity::class.java) {
                    putString(REQUEST, getString(R.string.rolls))
                }
            }
            R.id.nav_favorites -> {
                openActivity(FavoriteActivity::class.java)
            }
            R.id.nav_rate_us -> {
                viewModel.showRatingDialog()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onBackPressed() {
        val stackEntryCount = supportFragmentManager.backStackEntryCount
        if (stackEntryCount == 0) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                disposable.add(
                    backPressedSubject
                        .buffer(2, 1)
                        .map { Pair(it[0], it[1]) }
                        .map { (first, second) -> second - first < TimeUnit.SECONDS.toMillis(2) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { canExit ->
                            when (canExit) {
                                true -> {
                                    super.onBackPressed()
                                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
                                }
                                false -> shortToast(getString(R.string.press_back))
                            }
                        }
                )

                backPressedSubject.onNext(System.currentTimeMillis())
            }
        } else {
            when {
                drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
                else -> {
                    try {
                        super.onBackPressed()
                    } catch (e: IllegalStateException) {
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
        updateManager.unregisterListener(this)
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

    private fun goToGooglePlay() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL)))
        finish()
    }
}

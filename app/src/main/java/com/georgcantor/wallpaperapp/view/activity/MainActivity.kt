package com.georgcantor.wallpaperapp.view.activity

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.ALL_VIDEOS
import com.georgcantor.wallpaperapp.util.Constants.APP_URL
import com.georgcantor.wallpaperapp.util.Constants.AUDI_VIDEOS
import com.georgcantor.wallpaperapp.util.Constants.BLACK
import com.georgcantor.wallpaperapp.util.Constants.BLUE
import com.georgcantor.wallpaperapp.util.Constants.BMW_VIDEOS
import com.georgcantor.wallpaperapp.util.Constants.GRAY
import com.georgcantor.wallpaperapp.util.Constants.GREEN
import com.georgcantor.wallpaperapp.util.Constants.MERCEDES_VIDEOS
import com.georgcantor.wallpaperapp.util.Constants.RATING
import com.georgcantor.wallpaperapp.util.Constants.RED
import com.georgcantor.wallpaperapp.util.Constants.REQUEST
import com.georgcantor.wallpaperapp.util.Constants.THEME_PREF
import com.georgcantor.wallpaperapp.util.Constants.YELLOW
import com.georgcantor.wallpaperapp.view.fragment.AudiFragment
import com.georgcantor.wallpaperapp.view.fragment.BmwFragment
import com.georgcantor.wallpaperapp.view.fragment.CategoryFragment
import com.georgcantor.wallpaperapp.view.fragment.MercedesFragment
import com.georgcantor.wallpaperapp.view.fragment.videos.VideosFragment
import com.georgcantor.wallpaperapp.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val updateAvailable = MutableLiveData<Boolean>().apply { value = false }
    private val backPressedSubject = BehaviorSubject.createDefault(0L)
    private val disposable = CompositeDisposable()

    private lateinit var mercedesFragment: Fragment
    private lateinit var bmwFragment: Fragment
    private lateinit var audiFragment: Fragment
    private lateinit var categoryFragment: Fragment
    private lateinit var viewModel: MainViewModel
    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(
            when (PreferenceManager(this).getString(THEME_PREF)) {
                BLACK -> R.style.ThemeBlack
                BLUE -> R.style.ThemeBlue
                GRAY -> R.style.ThemeGray
                RED -> R.style.ThemeRed
                YELLOW -> R.style.ThemeYellow
                GREEN -> R.style.ThemeGreen
                else -> R.style.ThemeBlack
            },
            true
        )

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = getViewModel()
        viewModel.loadCategories()

        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) reviewInfo = task.result
        }

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

        viewModel.isRateDialogShow.observe(this, Observer { show ->
            if (show) showRatingDialog()
        })

        RATING = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
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
            R.id.action_bmw_videos -> {
                toolbar.title = getString(R.string.videos)
                openFragment(VideosFragment.create(BMW_VIDEOS), getString(R.string.bmw_videos))
            }
            R.id.action_audi_videos -> {
                toolbar.title = getString(R.string.videos)
                openFragment(VideosFragment.create(AUDI_VIDEOS), getString(R.string.audi_videos))
            }
            R.id.action_mercedes_videos -> {
                toolbar.title = getString(R.string.videos)
                openFragment(VideosFragment.create(MERCEDES_VIDEOS), getString(R.string.mercedes_benz_videos))
            }
            R.id.action_all_videos -> {
                toolbar.title = getString(R.string.videos)
                openFragment(VideosFragment.create(ALL_VIDEOS), getString(R.string.all_videos))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_aston -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.aston)) }
            R.id.nav_bentley -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.bentley)) }
            R.id.nav_bugatti -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.bugatti)) }
            R.id.nav_ferrari -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.ferrari)) }
            R.id.nav_lambo -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.lamborghini)) }
            R.id.nav_mclaren -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.mclaren)) }
            R.id.nav_porsche -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.porsche)) }
            R.id.nav_rolls -> openActivity(CarBrandActivity::class.java) { putString(REQUEST, getString(R.string.rolls)) }
            R.id.nav_favorites -> openActivity(FavoriteActivity::class.java)
            R.id.nav_rate_us -> showRatingDialog()
            R.id.nav_change_theme -> showThemeDialog(this::recreate)
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
        super.onDestroy()
        disposable.dispose()
        toasts.map(Toast::cancel)
        toasts.clear()
    }

    private fun showRatingDialog() {
        if (::reviewInfo.isInitialized) {
            reviewManager.launchReviewFlow(this, reviewInfo).addOnFailureListener {
                longToast(it.message ?: "OnFailureListener")
            }.addOnCompleteListener { result ->
                result.exception?.message?.let(this::longToast)
            }
        }
    }

    private fun goToGooglePlay() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL)))
        finish()
    }
}

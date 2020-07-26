package com.georgcantor.wallpaperapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.BLACK
import com.georgcantor.wallpaperapp.util.Constants.BLUE
import com.georgcantor.wallpaperapp.util.Constants.EXTRA_PIC
import com.georgcantor.wallpaperapp.util.Constants.GRAY
import com.georgcantor.wallpaperapp.util.Constants.GREEN
import com.georgcantor.wallpaperapp.util.Constants.INDEX
import com.georgcantor.wallpaperapp.util.Constants.IS_SWIPE_SHOW
import com.georgcantor.wallpaperapp.util.Constants.RED
import com.georgcantor.wallpaperapp.util.Constants.YELLOW
import com.georgcantor.wallpaperapp.view.adapter.ViewPagerAdapter
import com.georgcantor.wallpaperapp.view.fragment.detail.DetailFragment
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(
            when (PreferenceManager(this).getString(Constants.THEME_PREF)) {
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

        setContentView(R.layout.activity_detail)

        if (!this.isNetworkAvailable()) {
            noInternetAnimationView.showAnimation()
            longToast(getString(R.string.no_internet))
        }

        val pic = intent.getParcelableExtra<CommonPic>(EXTRA_PIC) as CommonPic

        viewModel = getViewModel()

        viewModel.getPictures(pic.tags ?: "", PreferenceManager(this).getInt(INDEX))

        viewModel.pictures.observe(this, Observer { pics ->
            ViewPagerAdapter(supportFragmentManager).apply {
                addFragment(DetailFragment.create(pic))
                pics.map {
                    if (it.url != pic.url) addFragment(DetailFragment.create(it))
                }
                view_pager.adapter = this
            }
        })

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                PreferenceManager(this@DetailActivity).saveBoolean(IS_SWIPE_SHOW, true)
            }
        })
    }
}

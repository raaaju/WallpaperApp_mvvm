package com.georgcantor.wallpaperapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.Constants.EXTRA_PIC
import com.georgcantor.wallpaperapp.util.Constants.INDEX
import com.georgcantor.wallpaperapp.util.Constants.IS_SWIPE_SHOW
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.view.adapter.ViewPagerAdapter
import com.georgcantor.wallpaperapp.view.fragment.detail.DetailFragment
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val pic = intent.getParcelableExtra(EXTRA_PIC) as CommonPic

        viewModel = getViewModel { parametersOf() }

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

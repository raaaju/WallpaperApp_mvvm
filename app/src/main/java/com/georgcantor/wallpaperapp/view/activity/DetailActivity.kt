package com.georgcantor.wallpaperapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.Constants.EXTRA_PIC
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

        viewModel.getPictures(pic.tags ?: "", 1)

        viewModel.pictures.observe(this, Observer { pics ->
            ViewPagerAdapter(supportFragmentManager).apply {
                addFragment(DetailFragment.create(pic))
                pics.map {
                    if (it.url != pic.url) addFragment(DetailFragment.create(it))
                }
                view_pager.adapter = this
            }
        })
    }
}

package com.georgcantor.wallpaperapp.ui.activity.detail

import android.Manifest
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityDetailBinding
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.ui.activity.BaseActivity
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : BaseActivity() {

    private val binding by viewBinding(ActivityDetailBinding::inflate)
    private val viewModel: DetailViewModel by viewModel()
    private var pic: CommonPic? = null
    private var permissionCheck = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        pic = intent.getParcelableExtra(PIC_EXTRA) as CommonPic?

        loadImage(pic?.imageURL, binding.image, binding.progressBar, R.color.black)

        Zoomy.Builder(this).target(binding.image).apply { register() }

        viewModel.isFavorite(pic?.url)

        viewModel.isFavorite.observe(this) {
            binding.bottomAppBar.menu.findItem(R.id.action_add_to_fav).setIcon(
                if (it) R.drawable.ic_star_red_24dp else R.drawable.ic_star_border
            )
        }

        binding.bottomAppBar.setNavigationOnClickListener { onBackPressed() }
        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_share -> share(pic?.imageURL)
                R.id.action_add_to_fav -> viewModel.addOrRemoveFromFavorites(pic)
            }
            true
        }

        binding.fab.setOnClickListener {
            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                setAsWallpaper()
            } else {
                requestPermission()
            }
        }
    }

    override fun onDestroy() {
        Zoomy.unregister(binding.image)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setAsWallpaper()
        } else {
            shortToast(getString(R.string.you_need_perm_toast))
        }
    }

    private fun requestPermission() {
        try {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                val requestCode = 102
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode
                )
            }
        } catch (e: IllegalStateException) {
        }
    }

    private fun setAsWallpaper() {
        val manager = WallpaperManager.getInstance(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                startActivity(Intent(manager.getCropAndSetWallpaperIntent(getImageUri(pic))))
            } catch (e: Exception) {
                val bitmap = getBitmap(pic)
                withContext(Dispatchers.Main) {
                    manager.setBitmap(bitmap)
                }
            }
        }
    }
}
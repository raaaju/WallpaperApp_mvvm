package com.georgcantor.wallpaperapp.ui.activity.detail

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
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
    private var isSave = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        pic = intent.getParcelableExtra(PIC_EXTRA) as CommonPic?

        pic?.imageURL?.let {
            loadImage(it, binding.image, binding.progressBar, R.color.black)
        } ?: run {
            pic?.url?.let { loadImage(it, binding.image, binding.progressBar, R.color.black) }
        }

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
                R.id.action_download -> { isSave = true; checkPermissions() }
                R.id.action_add_to_fav -> viewModel.addOrRemoveFromFavorites(pic)
            }
            true
        }

        binding.fab.setOnClickListener { isSave = false; checkPermissions() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            if (isSave) saveImage() else setAsWallpaper()
        } else {
            shortToast(getString(R.string.you_need_perm_toast))
        }
    }

    private fun checkPermissions() {
        permissionCheck = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        if (permissionCheck == PERMISSION_GRANTED) if (isSave) saveImage() else setAsWallpaper()
        else requestPermission()
    }

    private fun requestPermission() {
        try {
            if (permissionCheck != PERMISSION_GRANTED) {
                requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 102)
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
                withContext(Dispatchers.Main) { manager.setBitmap(getBitmap(pic)) }
            }
        }
    }

    private fun saveImage() {
        if (!isNetworkAvailable()) {
            shortToast(getString(R.string.no_internet))
            return
        }
        pic?.let { saveImage(it.imageURL.orEmpty(), binding.progressBar) }
    }
}
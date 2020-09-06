package com.georgcantor.wallpaperapp.view.fragment.detail

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.EXTRA_PIC
import com.georgcantor.wallpaperapp.util.Constants.IS_SWIPE_SHOW
import com.georgcantor.wallpaperapp.util.Constants.PREF_BOOLEAN
import com.georgcantor.wallpaperapp.view.activity.DetailActivity
import com.georgcantor.wallpaperapp.view.activity.FullScreenActivity
import com.georgcantor.wallpaperapp.view.adapter.SimilarAdapter
import com.georgcantor.wallpaperapp.viewmodel.DetailsViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class DetailFragment : Fragment(R.layout.fragment_detail) {

    companion object {
        fun create(pic: CommonPic): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply { putParcelable(EXTRA_PIC, pic) }
            }
        }
    }

    private var pic: CommonPic? = null
    private var permissionCheck: Int = 0
    private val disposable = CompositeDisposable()

    private lateinit var prefManager: PreferenceManager
    private lateinit var viewModel: DetailsViewModel
    private lateinit var zoomyBuilder: Zoomy.Builder

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabClock: Animation
    private lateinit var fabAnticlock: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf(requireActivity()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())

        fabOpen = loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = loadAnimation(requireContext(), R.anim.fab_close)
        fabClock = loadAnimation(requireContext(), R.anim.fab_rotate_clock)
        fabAnticlock = loadAnimation(requireContext(), R.anim.fab_rotate_anticlock)

        progress_anim?.showAnimation()

        if (!requireContext().isNetworkAvailable()) context?.longToast(getString(R.string.no_internet))

        initView()

        pic?.url?.let {
            viewModel.picInFavorites(it)
                .subscribe({ isFav ->
                    if (isFav) bottom_app_bar.menu.findItem(R.id.action_add_to_fav).setIcon(R.drawable.ic_star_red_24dp)
                }, {
                })
        }

        pic?.tags.let {
            viewModel.getSimilarImages(it ?: "")
        }

        viewModel.isProgressVisible.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> similar_progress.showAnimation()
                false -> similar_progress.hideAnimation()
            }
        })

        viewModel.pictures.observe(viewLifecycleOwner, Observer {
            similar_text.visibility = if (it.isNullOrEmpty()) GONE else VISIBLE
            val adapter = SimilarAdapter {
                context?.openActivity(DetailActivity::class.java) { putParcelable(EXTRA_PIC, it) }
            }
            similar_recycler.adapter = adapter
            adapter.pictures = it
        })

        zoomyBuilder = Zoomy.Builder(requireActivity())
            .target(image)
            .doubleTapListener {
                pic?.let {
                    viewModel.setFavoriteStatus(
                        it,
                        bottom_app_bar.menu.findItem(R.id.action_add_to_fav),
                        star_anim,
                        unstar_anim
                    )
                }
            }
        zoomyBuilder.register()

        viewModel.isFabOpened.observe(viewLifecycleOwner, Observer { open ->
            fab.setOnClickListener {
                if (!open) {
                    fab_full.visible()
                    fab_set_wall.visible()
                    fab_full.isClickable = true
                    fab_set_wall.isClickable = true
                    fab.startAnimation(fabClock)
                    fab_set_wall.startAnimation(fabOpen)
                    fab_full.startAnimation(fabOpen)
                    viewModel.setFabState(true)
                } else {
                    fab_full.gone()
                    fab_set_wall.gone()
                    fab_full.isClickable = false
                    fab_set_wall.isClickable = false
                    fab.startAnimation(fabAnticlock)
                    fab_set_wall.startAnimation(fabClose)
                    fab_full.startAnimation(fabClose)
                    viewModel.setFabState(false)
                }
            }
        })

        fab_set_wall.setOnClickListener {
            if (requireContext().isNetworkAvailable()) {
                if (permissionCheck == PERMISSION_GRANTED) {
                    progress_anim?.showAnimation()
                    setWallAsync()
                } else {
                    prefManager.saveBoolean(PREF_BOOLEAN, true)
                    checkSavingPermission(permissionCheck)
                }
            } else {
                context?.longToast(getString(R.string.no_internet))
            }
        }

        fab_full.setOnClickListener {
            val intent = Intent(requireContext(), FullScreenActivity::class.java)
            intent.putExtra(Constants.FULL_EXTRA, pic?.imageURL)
            intent.putExtra(Constants.IS_PORTRAIT, pic?.heght ?: 0 > pic?.width ?: 0)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        bottom_app_bar.setNavigationOnClickListener { activity?.onBackPressed() }

        bottom_app_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_share -> context?.share(pic?.imageURL)
                R.id.action_download -> startDownloading()
                R.id.action_add_to_fav -> {
                    pic?.let { pic ->
                        viewModel.setFavoriteStatus(pic, it, star_anim, unstar_anim)
                    }
                }
            }
            true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            ORIENTATION_LANDSCAPE -> {
                similar_text.gone()
                similar_recycler.gone()
            }
            ORIENTATION_PORTRAIT -> {
                similar_text.visible()
                similar_recycler.visible()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            when (prefManager.getBoolean(PREF_BOOLEAN)) {
                true -> {
                    prefManager.saveBoolean(PREF_BOOLEAN, false)
                    setWallAsync()
                }
                false -> pic?.let { pic?.let { context?.saveImage(it.imageURL ?: "") } }
            }
        } else {
            val intent = Intent()
            intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            startActivity(intent)
            activity?.finish()
            activity?.longToast(getString(R.string.you_need_perm_toast))
        }
    }

    override fun onDestroy() {
        try {
            activity?.unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
        }
        image?.let { Zoomy.unregister(it) }
        disposable.dispose()
        super.onDestroy()
    }

    private fun checkSavingPermission(permissionCheck: Int) {
        try {
            if (permissionCheck != PERMISSION_GRANTED) {
                val requestCode = 102
                requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), requestCode)
            }
        } catch (e: IllegalStateException) {
        }
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            context.shortToast(getString(R.string.down_complete))
            try {
                progress_anim?.hideAnimation()
            } catch (e: IndexOutOfBoundsException) {
                progress_anim?.hideAnimation()
            }
        }
    }

    private fun initView() {
        permissionCheck = checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE)

        pic = arguments?.get(EXTRA_PIC) as CommonPic

        pic?.let { pic ->
            pic.imageURL?.let { context?.loadImage(it, image, progress_anim) }
        }

        val filter = IntentFilter(ACTION_DOWNLOAD_COMPLETE)
        activity?.registerReceiver(downloadReceiver, filter)

        val isSwipeShow = prefManager.getBoolean(IS_SWIPE_SHOW)
        if (!isSwipeShow) swipe_anim.showSeveralAnimation(1F, 3)
    }

    private fun setWallAsync() {
        progress_anim?.showAnimation()

        pic?.let { pic ->
            disposable.add(
                viewModel.getBitmapAsync(pic)
                    .subscribe({
                        val wallpaperManager = WallpaperManager.getInstance(activity?.baseContext)
                        it?.let { bitmap ->
                            viewModel.getImageUri(bitmap)
                                .subscribe({ uri ->
                                    try {
                                        startActivity(Intent(wallpaperManager.getCropAndSetWallpaperIntent(uri)))
                                    } catch (e: IllegalArgumentException) {
                                        try {
                                            it.let { bitMap ->
                                                viewModel.getImageUri(bitMap)
                                                    .subscribe({ uri ->
                                                        val bitmap2 =
                                                            MediaStore.Images.Media.getBitmap(
                                                                activity?.contentResolver,
                                                                uri
                                                            )
                                                        viewModel.setBitmapAsync(bitmap2)
                                                    }, {
                                                        context?.shortToast(getString(R.string.something_went_wrong))
                                                    })
                                            }
                                        } catch (e: OutOfMemoryError) {
                                            context?.shortToast(getString(R.string.something_went_wrong))
                                        }
                                    }
                                }, { throwable ->
                                    context?.longToast(throwable.message.toString())
                                })
                        }
                        context?.longToast(getString(R.string.wallpaper_is_install))
                    }, {
                        context?.shortToast(getString(R.string.something_went_wrong))
                    })
            )
        }
    }

    private fun startDownloading() {
        if (!requireContext().isNetworkAvailable()) {
            context?.shortToast(getString(R.string.no_internet))
            return
        }
        progress_anim.showAnimation()

        if (permissionCheck == PERMISSION_GRANTED) {
            pic?.let { context?.saveImage(it.imageURL ?: "") }
        } else {
            checkSavingPermission(permissionCheck)
        }

        context?.shortToast(getString(R.string.download_start))
        Handler().postDelayed({
            context?.shortToast(getString(R.string.down_complete))
            progress_anim?.hideAnimation()
        }, 5000)
    }
}
package com.georgcantor.wallpaperapp.ui.fragment

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper
import com.georgcantor.wallpaperapp.network.NetworkUtils
import com.georgcantor.wallpaperapp.ui.adapter.TagAdapter
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_detail.*
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class DetailFragment : Fragment() {

    companion object {
        const val EXTRA_PIC = "picture"
        const val ORIGIN = "caller"
    }

    private var hit: Hit? = null
    private val tags = ArrayList<String>()
    private var first = 0
    private lateinit var networkUtils: NetworkUtils
    private lateinit var tagAdapter: TagAdapter
    private var file: File? = null
    private var tagTitle: TextView? = null
    private var permissionCheck: Int = 0
    private var db: DatabaseHelper? = null
    private var isDownloaded = false
    private var isCallerCollection = false
    private var pathOfFile: String? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkUtils = NetworkUtils(requireContext())
        progressBarDetail?.visibility = View.VISIBLE

        db = DatabaseHelper(requireContext())

        initView()

        fabDownload?.setOnClickListener(View.OnClickListener {
            if (!fileIsExist()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission()
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        return@OnClickListener
                    }
                }
                if (networkUtils.isInternetConnectionPresent) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(R.string.download)
                    builder.setIcon(R.drawable.ic_download)
                    builder.setMessage(R.string.choose_format)

                    builder.setPositiveButton("HD") { _, _ ->
                        progressBarDetail?.visibility = View.VISIBLE
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.webformatURL
                                val imageUri = Uri.parse(uri)
                                downloadData(imageUri)
                                fabDownload?.setImageDrawable(VectorDrawableCompat.create(resources,
                                        R.drawable.ic_photo, null))
                            } else {
                                Toast.makeText(requireContext(), resources
                                        .getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                progressBarDetail?.visibility = View.GONE
                            }
                        }
                    }

                    builder.setNeutralButton(hit?.imageWidth.toString() + " x "
                            + hit?.imageHeight) { _, _ ->
                        progressBarDetail?.visibility = View.VISIBLE
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.imageURL
                                val imageUri = Uri.parse(uri)
                                downloadData(imageUri)
                                fabDownload?.setImageDrawable(ResourcesCompat.getDrawable(
                                        requireActivity().resources,
                                        R.drawable.ic_photo,
                                        null
                                ))
                            } else {
                                Toast.makeText(requireContext(), resources
                                        .getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                progressBarDetail?.visibility = View.GONE
                            }
                        }
                    }

                    builder.setNegativeButton("FullHD") { _, _ ->
                        progressBarDetail?.visibility = View.VISIBLE
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!fileIsExist()) {
                                val uri = hit?.fullHDURL
                                val imageUri = Uri.parse(uri)
                                downloadData(imageUri)
                                fabDownload?.setImageDrawable(requireActivity().resources
                                        .getDrawable(R.drawable.ic_photo))
                            } else {
                                Toast.makeText(requireContext(), resources
                                        .getString(R.string.image_downloaded),
                                        Toast.LENGTH_SHORT).show()
                                progressBarDetail?.visibility = View.GONE
                            }
                        }
                    }
                    builder.create().show()
                } else {
                    Toast.makeText(requireContext(), resources.getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show()
                }

            } else {
                checkWallpPermission()
                val uri = Uri.fromFile(file)
                pathOfFile = UtilityMethods.getPath(requireContext(), uri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setAsWallpaper6(pathOfFile)
                } else {
                    setAsWallpaper()
                }
            }
        })
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, tags[0] + resources.getString(R.string.down_complete),
                    Toast.LENGTH_SHORT).show()
            isDownloaded = true
            progressBarDetail?.visibility = View.GONE
        }
    }

    private fun setAsWallpaper() {
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_ATTACH_DATA)
        intent.setDataAndType(uri, resources.getString(R.string.image_jpg))
        intent.putExtra(resources.getString(R.string.mimeType), resources.getString(R.string.image_jpg))

        startActivityForResult(Intent.createChooser(intent,
                resources.getString(R.string.Set_As)), 200)
    }

    private fun setAsWallpaper6(pathOfFile: String?) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels shl 1

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathOfFile, options)

        options.inSampleSize = calculateInSampleSize(options, width, height)

        options.inJustDecodeBounds = false
        val decodedSampleBitmap = BitmapFactory.decodeFile(pathOfFile, options)

        val manager = WallpaperManager.getInstance(requireContext())
        try {
            manager.setBitmap(decodedSampleBitmap)
            Toast.makeText(requireContext(), getString(R.string.wallpaper_is_install), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int,
                                      reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()

            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        return inSampleSize
    }

    private fun initView() {
        permissionCheck = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (arguments?.containsKey(EXTRA_PIC) == true) {
            hit = arguments?.get(EXTRA_PIC) as Hit
        } else {
            throw IllegalArgumentException("Detail activity must receive a Hit parcelable")
        }
        hit?.let {
            var title = it.tags
            while (title.contains(",")) {
                val element = title.substring(0, title.indexOf(","))
                tags.add(element)
                first = title.indexOf(",")
                title = title.substring(++first)
            }
            tags.add(title)
        }

        tagTitle?.text = tags[0]
        tagsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false)
        tagAdapter = TagAdapter(requireContext())
        tagAdapter.setTagList(tags)
        tagsRecyclerView.adapter = tagAdapter
        file = File(Environment.getExternalStoragePublicDirectory("/" + resources
                .getString(R.string.app_name)), hit?.id.toString() + resources
                .getString(R.string.jpg))
        if (fileIsExist()) {
            fabDownload?.setImageDrawable(VectorDrawableCompat.create(resources,
                    R.drawable.ic_photo, null))
        }

        if (arguments?.containsKey(ORIGIN) == true) {
            Picasso.with(requireContext())
                    .load(file)
                    .placeholder(R.drawable.plh)
                    .into(detailImageView)
            isCallerCollection = true
        } else {
            Picasso.with(requireContext())
                    .load(hit?.webformatURL)
                    .placeholder(R.drawable.plh)
                    .into(detailImageView, object : Callback {
                        override fun onSuccess() {
                            progressBarDetail?.visibility = View.GONE
                        }

                        override fun onError() {
                            progressBarDetail?.visibility = View.GONE
                            Toast.makeText(requireContext(), getString(R.string.something_went_wrong),
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
        }

        nameTextView.text = hit?.user
        downloadsTextView.text = hit?.downloads.toString()
        favoritesTextView.text = hit?.favorites.toString()
        if (!networkUtils.isInternetConnectionPresent) {
            Picasso.with(requireContext())
                    .load(R.drawable.memb)
                    .transform(CropCircleTransformation())
                    .into(detailImageView)
        } else {
            hit?.let {
                if (it.userImageURL.isNotEmpty()) {
                    Picasso.with(requireContext())
                            .load(hit?.userImageURL)
                            .transform(CropCircleTransformation())
                            .into(userImageView)
                } else {
                    Picasso.with(requireContext())
                            .load(R.drawable.memb)
                            .transform(CropCircleTransformation())
                            .into(userImageView)
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        requireActivity().registerReceiver(downloadReceiver, filter)
    }

    private fun downloadData(uri: Uri): Long {
        val downloadReference: Long
        val downloadManager =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        var name = Environment.getExternalStorageDirectory().absolutePath
        name += "/YourDirectoryName/"

        val request = DownloadManager.Request(uri)

        try {
            request.setTitle(tags[0] + resources.getString(R.string.down))
            request.setDescription(resources.getString(R.string.down_wallpapers))
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                request.setDestinationInExternalPublicDir("/" + resources
                        .getString(R.string.app_name), hit?.id.toString() + resources
                        .getString(R.string.jpg))
            }
        } catch (e: IllegalStateException) {
            Toast.makeText(requireContext(), R.string.something_went_wrong, Toast.LENGTH_LONG).show()
        }
        downloadReference = downloadManager.enqueue(request)

        return downloadReference
    }

    public override fun onDestroy() {
        try {
            requireActivity().unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun fileIsExist(): Boolean {
        return file?.exists() ?: false
    }

    private fun checkPermission() {
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            val requestCode = 102
            ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
        }
    }

    private fun checkWallpPermission() {
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.SET_WALLPAPER), 103)
        }
    }

//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>,
//                                            grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            restartActivity()
//        } else {
//            val intent = Intent()
//            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//            val uri = Uri.fromParts("package", packageName, null)
//            intent.data = uri
//            startActivity(intent)
//            finish()
//            Toast.makeText(requireContext(), R.string.you_need_perm_toast, Toast.LENGTH_LONG).show()
//        }
//    }

    private fun addToFavorite(imageUrl: String, hdUrl: String) {
        db?.insertToFavorites(imageUrl, hdUrl)
    }
}
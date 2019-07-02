package com.georgcantor.wallpaperapp.ui.fragment

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper
import com.georgcantor.wallpaperapp.network.NetworkUtilities
import com.georgcantor.wallpaperapp.ui.adapter.TagAdapter
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
    private lateinit var networkUtilities: NetworkUtilities
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

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, tags[0] + resources.getString(R.string.down_complete),
                    Toast.LENGTH_SHORT).show()
            isDownloaded = true
            progressBarDetail?.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
}
package com.georgcantor.wallpaperapp.util

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.roundToInt

class ScalableImageView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val arr = FloatArray(9)
    private val mat = Matrix()
    private val last = PointF()
    private val start = PointF()
    private val minScale = 0.5F
    private val maxScale = 4f
    private var mode = NONE
    private var redundantXSpace = 0F
    private var redundantYSpace = 0F
    private var saveScale = 1F
    private var right = 0F
    private var bottom = 0F
    private var originalBitmapWidth = 0F
    private var originalBitmapHeight = 0F
    private var scaleDetector: ScaleGestureDetector? = null

    private val bmWidth: Int
        get() { return drawable?.intrinsicWidth ?: 0 }

    private val bmHeight: Int
        get() { return drawable?.intrinsicHeight ?: 0 }

    init {
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        imageMatrix = mat
        scaleType = ScaleType.MATRIX
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val bmHeight = bmHeight
        val bmWidth = bmWidth
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        val scale = if (width > height) height / bmHeight else width / bmWidth

        mat.setScale(scale, scale)
        saveScale = 1f
        originalBitmapWidth = scale * bmWidth
        originalBitmapHeight = scale * bmHeight
        redundantYSpace = height - originalBitmapHeight
        redundantXSpace = width - originalBitmapWidth
        mat.postTranslate(redundantXSpace / 2, redundantYSpace / 2)
        imageMatrix = mat
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector?.onTouchEvent(event)
        mat.getValues(arr)
        val x = arr[Matrix.MTRANS_X]
        val y = arr[Matrix.MTRANS_Y]
        val curr = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                last[event.x] = event.y
                start.set(last)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                last[event.x] = event.y
                start.set(last)
                mode = ZOOM
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == ZOOM || mode == DRAG && saveScale > minScale) {
                    var deltaX = curr.x - last.x
                    var deltaY = curr.y - last.y
                    val scaleWidth = (originalBitmapWidth * saveScale).roundToInt().toFloat()
                    val scaleHeight = (originalBitmapHeight * saveScale).roundToInt().toFloat()
                    var limitX = false
                    var limitY = false

                    if (scaleWidth < width && scaleHeight < height) {
                        // don't do anything
                    } else if (scaleWidth < width) {
                        deltaX = 0f
                        limitY = true
                    } else if (scaleHeight < height) {
                        deltaY = 0f
                        limitX = true
                    } else {
                        limitX = true
                        limitY = true
                    }
                    if (limitY) {
                        if (y + deltaY > 0) {
                            deltaY = -y
                        } else if (y + deltaY < -bottom) {
                            deltaY = -(y + bottom)
                        }
                    }
                    if (limitX) {
                        if (x + deltaX > 0) {
                            deltaX = -x
                        } else if (x + deltaX < -right) {
                            deltaX = -(x + right)
                        }
                    }
                    mat.postTranslate(deltaX, deltaY)
                    last[curr.x] = curr.y
                }
            }
            MotionEvent.ACTION_UP -> {
                mode = NONE
                val xDiff = abs(curr.x - start.x).toInt()
                val yDiff = abs(curr.y - start.y).toInt()
                if (xDiff < CLICK && yDiff < CLICK) performClick()
            }
            MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        imageMatrix = mat
        invalidate()

        return true
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = saveScale * scaleFactor
            if (newScale < maxScale && newScale > minScale) {
                saveScale = newScale
                val width = width.toFloat()
                val height = height.toFloat()
                right = originalBitmapWidth * saveScale - width
                bottom = originalBitmapHeight * saveScale - height
                val scaledBitmapWidth = originalBitmapWidth * saveScale
                val scaledBitmapHeight = originalBitmapHeight * saveScale
                if (scaledBitmapWidth <= width || scaledBitmapHeight <= height) {
                    mat.postScale(scaleFactor, scaleFactor, width / 2, height / 2)
                } else {
                    mat.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                }
            }
            return true
        }
    }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
        private const val CLICK = 3
    }
}

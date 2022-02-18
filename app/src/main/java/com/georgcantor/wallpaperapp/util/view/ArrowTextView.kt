package com.georgcantor.wallpaperapp.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ViewTextWithRigthArrowBinding

class ArrowTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        ViewTextWithRigthArrowBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setupAttrs(attrs)
    }

    private fun setupAttrs(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.ArrowTextView, 0, 0
        )
        try {
            val text = attributes.getString(R.styleable.ArrowTextView_arrowTextViewText)
            binding.textViewActionText.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            attributes.recycle()
        }
    }

    fun setText(text: CharSequence) {
        binding.textViewActionText.text = text
    }

    fun setText(@StringRes resId: Int) {
        binding.textViewActionText.setText(resId)
    }
}
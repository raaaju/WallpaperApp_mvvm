package com.georgcantor.wallpaperapp.util.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.remote.response.LoadableResult
import com.georgcantor.wallpaperapp.model.remote.response.ParsedError

class StateViewFlipper(context: Context, attrs: AttributeSet? = null) : ViewFlipper(context, attrs) {

    enum class State(val displayedChild: Int) {
        LOADING(0), ERROR(1), EMPTY(2), DATA(3)
    }

    private var state = State.LOADING
    private val loadingView: View
    private val errorView: View
    private val imageError: ImageView?
    private val textErrorRetryTitle: AppCompatTextView?
    private val textErrorTitle: AppCompatTextView?
    private val textViewErrorAction: ArrowTextView?
    private val emptyView: View
    private val imageEmpty: ImageView?
    private val textEmptyComment: AppCompatTextView?
    private val textEmptyTitle: AppCompatTextView?
    private val textViewEmptyAction: ArrowTextView?
    private val disabledStates = mutableListOf<State>()

    init {
        val layoutInflater = LayoutInflater.from(context)
        val layoutResProvider = LayoutResProvider(context, attrs)

        loadingView = layoutInflater.inflate(layoutResProvider.loadingRes, this, false)
        addView(loadingView)

        errorView = layoutInflater.inflate(layoutResProvider.errorRes, this, false)
        imageError = errorView.findViewById(R.id.imageError)
        textErrorRetryTitle = errorView.findViewById(R.id.textErrorRetryTitle)
        textErrorTitle = errorView.findViewById(R.id.textErrorTitle)
        textViewErrorAction = errorView.findViewById(R.id.textViewErrorAction)
        setupErrorView(layoutResProvider)
        addView(errorView)

        emptyView = layoutInflater.inflate(layoutResProvider.emptyRes, this, false)
        imageEmpty = emptyView.findViewById(R.id.imageEmpty)
        textEmptyComment = emptyView.findViewById(R.id.textEmptyComment)
        textEmptyTitle = emptyView.findViewById(R.id.textEmptyTitle)
        textViewEmptyAction = emptyView.findViewById(R.id.textViewEmptyAction)
        setupEmptyView(layoutResProvider)
        addView(emptyView)

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.StateViewFlipper)
            array.recycle()
        }
    }

    private fun setupErrorView(provider: LayoutResProvider) {
        val color = ContextCompat.getColor(context, provider.errorViewsColorRes)
        textViewErrorAction?.let { arrowTextView ->
            arrowTextView.findViewById<ImageView>(R.id.imageViewArrow)?.let {
                ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(color))
            }
            arrowTextView.findViewById<AppCompatTextView>(R.id.textViewActionText)?.setTextColor(color)
        }
        textErrorTitle?.setTextColor(color)
        textErrorRetryTitle?.setTextColor(color)
        imageError?.let { ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(color)) }
    }

    private fun setupEmptyView(provider: LayoutResProvider) {
        val color = ContextCompat.getColor(context, provider.errorViewsColorRes)
        textViewEmptyAction?.let { arrowTextView ->
            arrowTextView.findViewById<ImageView>(R.id.imageViewArrow)?.let {
                ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(color))
            }
            arrowTextView.findViewById<AppCompatTextView>(R.id.textViewActionText)?.setTextColor(color)
        }
        textEmptyTitle?.setTextColor(color)
        textEmptyComment?.setTextColor(color)
        imageEmpty?.let { ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(color)) }
    }

    fun <T> setStateFromResult(loadableResult: LoadableResult<T>) {
        when (loadableResult) {
            is LoadableResult.Loading -> setStateLoading(loadableResult.fullScreen)
            is LoadableResult.Failure -> setStateError(loadableResult.error)
            is LoadableResult.Empty -> { }
            is LoadableResult.Success -> setStateData()
        }
    }

    fun setRetryMethod(onRetryReceived: () -> Unit) {
        textViewErrorAction?.setOnClickListener { onRetryReceived.invoke() }
    }

    fun setEmptyMethod(onEmptyReceived: () -> Unit) {
        textViewEmptyAction?.setOnClickListener { onEmptyReceived.invoke() }
    }

    fun setEmptyState() {
        changeState(State.EMPTY)
    }

    fun setEmptyStateWithTitles(emptyTitle: String, emptyComment: String, actionText: String) {
        textEmptyTitle?.text = emptyTitle
        textEmptyComment?.text = emptyComment
        textViewEmptyAction?.setText(actionText)
        changeState(State.EMPTY)
    }

    fun currentState() = state

    fun disableState(vararg states: State) {
        for (state in states) {
            if (stateIsDisabled(state)) continue
            disabledStates.add(state)
            getChildAt(state.displayedChild)?.isVisible = false
        }
    }

    private fun changeState(newState: State) {
        if (stateIsDisabled(newState)) return
        if (state != newState || displayedChild != newState.displayedChild) {
            state = newState
            displayedChild = newState.displayedChild
        }
    }

    fun setStateLoading(fullScreen: Boolean = true) {
        if (fullScreen) changeState(State.LOADING)
    }

    fun setStateError(error: ParsedError? = null) {
        changeState(State.ERROR)
        error?.let { notNullError -> setErrorStateContent(notNullError) }
    }

    fun setStateData() {
        changeState(State.DATA)
    }

    fun setErrorStateContent(error: ParsedError) {
        textErrorTitle?.text = error.title
        textErrorRetryTitle?.text = error.message
        textViewErrorAction?.setText(error.button)
    }

    private fun stateIsDisabled(state: State): Boolean {
        return disabledStates.contains(state)
    }

    private class LayoutResProvider(context: Context, attrs: AttributeSet?) {

        companion object {
            @LayoutRes
            val DEFAULT_ERROR_LAYOUT = R.layout.view_state_error

            @LayoutRes
            val DEFAULT_LOADING_LAYOUT = R.layout.view_state_loading

            @LayoutRes
            val DEFAULT_EMPTY_LAYOUT = R.layout.view_state_empty

            @ColorRes
            val DEFAULT_ERROR_VIEWS_COLOR = R.color.white
        }

        @LayoutRes
        val loadingRes: Int

        @LayoutRes
        val errorRes: Int

        @LayoutRes
        val emptyRes: Int

        @ColorRes
        val errorViewsColorRes: Int

        init {
            if (attrs != null) {
                val array = context.obtainStyledAttributes(attrs, R.styleable.StateViewFlipper)
                val collapseStatesToTop = array.getBoolean(R.styleable.StateViewFlipper_collapseStatesToTop, false)
                val loadingId = array.getResourceId(R.styleable.StateViewFlipper_loadingLayoutRes, -1)
                val errorColor = array.getResourceId(R.styleable.StateViewFlipper_errorViewsColor, -1)

                loadingRes = if (loadingId == -1 && collapseStatesToTop) R.layout.view_state_loading_top
                else if (loadingId == -1) DEFAULT_LOADING_LAYOUT
                else loadingId

                errorRes = array.getResourceId(R.styleable.StateViewFlipper_errorLayoutRes, DEFAULT_ERROR_LAYOUT)
                emptyRes = array.getResourceId(R.styleable.StateViewFlipper_emptyLayoutRes, DEFAULT_EMPTY_LAYOUT)
                errorViewsColorRes = if (errorColor == -1) DEFAULT_ERROR_VIEWS_COLOR else errorColor
                array.recycle()
            } else {
                loadingRes = DEFAULT_LOADING_LAYOUT
                errorRes = DEFAULT_ERROR_LAYOUT
                emptyRes = DEFAULT_EMPTY_LAYOUT
                errorViewsColorRes = DEFAULT_ERROR_VIEWS_COLOR
            }
        }
    }
}
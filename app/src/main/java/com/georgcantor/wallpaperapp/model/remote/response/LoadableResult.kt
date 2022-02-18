package com.georgcantor.wallpaperapp.model.remote.response

import com.georgcantor.wallpaperapp.util.parseError

typealias Loading<T> = LoadableResult.Loading<T>
typealias Success<T> = LoadableResult.Success<T>
typealias Failure<T> = LoadableResult.Failure<T>

sealed class LoadableResult<R> {

    class Loading<R>(var fullScreen: Boolean = true) : LoadableResult<R>()
    class Success<R>(val value: R) : LoadableResult<R>()
    class Empty<R> : LoadableResult<R>()
    class Failure<R>(val throwable: Throwable, error: ParsedError? = null) : LoadableResult<R>() {
        val error = error ?: throwable.parseError()
    }

    companion object {
        fun <R> loading(fullScreen: Boolean = true): LoadableResult<R> = Loading(fullScreen)
        fun <R> empty(): LoadableResult<R> = Empty()
        fun <R> success(value: R): LoadableResult<R> = Success(value)
        fun <R> failure(throwable: Throwable, error: ParsedError? = null): LoadableResult<R> =
            Failure(throwable, error)
    }

    val isLoading: Boolean get() = this is Loading

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure

    val isEmpty: Boolean get() = this is Empty

    fun getOrNull(): R? = when (this) {
        is Success -> value
        else -> null
    }

    fun getOrDefault(default: () -> R): R = when (this) {
        is Success -> value
        else -> default()
    }

    fun getOrDefault(defaultValue: R): R = when (this) {
        is Success -> value
        else -> defaultValue
    }

    inline fun <C> map(f: (R) -> C): LoadableResult<C> = when (this) {
        is Loading -> loading(fullScreen)
        is Failure -> failure(throwable)
        is Success -> success(f(value))
        is Empty -> empty()
        else -> empty()
    }

    inline fun <C> fold(
        ifLoading: () -> C,
        ifFailure: (t: Throwable) -> C,
        ifSuccess: (R) -> C,
        ifEmpty: () -> C,
    ): C = when (this) {
        is Loading -> ifLoading()
        is Failure -> ifFailure(throwable)
        is Success -> ifSuccess(value)
        is Empty -> ifEmpty()
        else -> ifEmpty()
    }

    inline fun doOnComplete(operation: (LoadableResult<R>) -> Unit) {
        when (this) {
            is Loading -> {}
            is Failure -> operation(failure(throwable))
            is Success -> operation(success(value))
        }
    }

    inline fun doOnLoading(operation: (fullScreen: Boolean) -> Unit) {
        when (this) {
            is Loading -> operation(this.fullScreen)
            is Failure -> {}
            is Success -> {}
        }
    }

    inline fun doOnSuccess(operation: (R) -> Unit) {
        when (this) {
            is Loading -> {}
            is Failure -> {}
            is Success -> operation(value)
        }
    }

    inline fun doOnFailure(operation: (ParsedError) -> Unit) {
        when (this) {
            is Loading -> {}
            is Failure -> operation(error)
            is Success -> {}
        }
    }

    inline fun doOnEmpty(operation: (ParsedError) -> Unit) {
        when (this) {
            is Loading -> {}
            is Failure -> operation(error)
            is Success -> {}
        }
    }
}
package com.georgcantor.wallpaperapp.model.remote.response

sealed class ParsedError(val code: Int, val title: String, val message: String, val button: String)

const val NETWORK_ERROR_CODE = 666
lateinit var NETWORK_ERROR_TITLE: String
lateinit var NETWORK_ERROR_MESSAGE: String
lateinit var NETWORK_ERROR_BUTTON: String

class NetworkError(
    code: Int,
    title: String,
    message: String,
    button: String
) : ParsedError(code, title, message, button)

const val DEFAULT_ERROR_CODE = 1
lateinit var DEFAULT_ERROR_TITLE: String
lateinit var DEFAULT_ERROR_MESSAGE: String
lateinit var DEFAULT_ERROR_BUTTON: String

class GeneralError(
    code: Int,
    title: String,
    message: String,
    button: String
) : ParsedError(code, title, message, button)
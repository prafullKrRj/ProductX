package com.prafullkumar.productx.utils

sealed class BaseResponse<out T> {
    data object Loading : BaseResponse<Nothing>()
    data class Success<T>(val data: T) : BaseResponse<T>()
    data class Error<T>(val message: String, val cachedData: T? = null) : BaseResponse<T>()
}
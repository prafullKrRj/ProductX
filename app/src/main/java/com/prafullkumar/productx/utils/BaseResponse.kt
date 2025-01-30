package com.prafullkumar.productx.utils

/**
 * A sealed class representing the different states of a response.
 */
sealed class BaseResponse<out T> {
    /**
     * Represents a loading state.
     */
    data object Loading : BaseResponse<Nothing>()

    /**
     * Represents a successful response with data.
     *
     * @param T The type of the data.
     * @property data The data of the response.
     */
    data class Success<T>(val data: T) : BaseResponse<T>()

    /**
     * Represents an error response with an optional cached data.
     *
     * @param T The type of the cached data.
     * @property message The error message.
     * @property cachedData The optional cached data.
     */
    data class Error<T>(val message: String, val cachedData: T? = null) : BaseResponse<T>()
}
package com.cornellappdev.android.eatery.data.models

/**
 * A generic wrapper for repository operations that can succeed or fail.
 * This allows ViewModels to handle errors gracefully and inform the UI accordingly.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: NetworkError) : Result<Nothing>()
}

sealed class NetworkError {
    object Unauthorized : NetworkError() {
        override fun toString() = "Authentication failed. Please log in again."
    }

    object NetworkFailure : NetworkError() {
        override fun toString() = "Network error. Please check your connection and try again."
    }

    data class ServerError(val code: Int, val message: String?) : NetworkError() {
        override fun toString() = "Server error ($code): ${message ?: "Unknown error"}"
    }

    object Timeout : NetworkError() {
        override fun toString() = "Request timed out. Please try again."
    }

    data class Unknown(val throwable: Throwable) : NetworkError() {
        override fun toString() = "An unexpected error occurred: ${throwable.message}"
    }
}



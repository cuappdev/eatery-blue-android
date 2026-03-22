package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.Result
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

internal suspend fun <T> safeNetworkRequest(request: suspend () -> T): Result<T> {
    return try {
        Result.Success(request())
    } catch (exception: Exception) {
        Result.Error(mapExceptionToNetworkError(exception))
    }
}

internal suspend fun <T> tryRequestWithTokenRefresh(
    request: suspend () -> T,
    refreshTokens: suspend () -> Result<Unit>
): Result<T> {
    return try {
        Result.Success(request())
    } catch (initialException: Exception) {
        // Only attempt refresh for auth-related errors
        val initialError = mapExceptionToNetworkError(initialException)
        if (initialError !is NetworkError.Unauthorized) {
            return Result.Error(initialError)
        }
        when (val refreshResult = refreshTokens()) {
            is Result.Success -> {
                try {
                    Result.Success(request())
                } catch (retryException: Exception) {
                    Result.Error(mapExceptionToNetworkError(retryException))
                }
            }

            is Result.Error -> Result.Error(refreshResult.error)
        }
    }
}

private fun mapExceptionToNetworkError(exception: Exception): NetworkError = when (exception) {
    is HttpException -> when (exception.code()) {
        401, 403 -> NetworkError.Unauthorized
        in 400..599 -> NetworkError.ServerError(exception.code(), exception.message())
        else -> NetworkError.Unknown(exception)
    }

    is SocketTimeoutException -> NetworkError.Timeout
    is IOException -> NetworkError.NetworkFailure
    else -> NetworkError.Unknown(exception)
}


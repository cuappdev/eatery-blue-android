package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.Result
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Wraps a suspend network request in a [Result] and maps thrown exceptions to [NetworkError]s.
 */
internal suspend fun <T> resultOfNetworkCall(request: suspend () -> T): Result<T> {
    return try {
        Result.Success(request())
    } catch (exception: Exception) {
        val networkError = when (exception) {
            is HttpException -> when (exception.code()) {
                401, 403 -> NetworkError.Unauthorized
                in 400..599 -> NetworkError.ServerError(exception.code(), exception.message())
                else -> NetworkError.Unknown(exception)
            }

            is SocketTimeoutException -> NetworkError.Timeout
            is IOException -> NetworkError.NetworkFailure
            else -> NetworkError.Unknown(exception)
        }
        Result.Error(networkError)
    }
}
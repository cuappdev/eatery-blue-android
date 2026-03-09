package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.DeviceId
import com.cornellappdev.android.eatery.data.models.LoginPIN
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.RefreshRequest
import com.cornellappdev.android.eatery.data.models.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AuthTokenRepository @Inject constructor(
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val _tokensConfiguredFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * A [StateFlow] that emits whether tokens have been configured successfully.
     */
    val tokensConfiguredFlow: StateFlow<Boolean> = _tokensConfiguredFlow.asStateFlow()

    suspend fun getDeviceId(): String = userPreferencesRepository.getOrCreateDeviceId()

    /**
     * Fetches initial tokens from the API based on device ID.
     * Called on app launch.
     */
    suspend fun getTokens(): Result<Unit> = safeRequest {
        val deviceId = getDeviceId()
        val response = networkApi.verifyToken(DeviceId(deviceId))
        val accessToken = response.accessToken
        val refreshToken = response.refreshToken
        if (accessToken != null) {
            userPreferencesRepository.setAccessToken(accessToken)
        } else {
            throw Exception("Access token is null")
        }
        if (refreshToken != null) {
            userPreferencesRepository.setRefreshToken(refreshToken)
        } else {
            throw Exception("Refresh token is null")
        }
    }

    fun markTokensAsConfigured() {
        _tokensConfiguredFlow.value = true
    }

    suspend fun refreshTokens(): Result<Unit> = safeRequest {
        val deviceId = getDeviceId()
        val refreshToken = userPreferencesRepository.refreshTokenFlow.firstOrNull()
            ?: throw IllegalStateException("Refresh token not available")
        val tokens = networkApi.refreshToken(
            RefreshRequest(
                deviceId = deviceId,
                refreshToken = refreshToken
            )
        )
        val accessToken = tokens.accessToken
        val newRefreshToken = tokens.refreshToken
        if (accessToken != null) {
            userPreferencesRepository.setAccessToken(accessToken)
        } else {
            throw Exception("Access token is null")
        }
        if (newRefreshToken != null) {
            userPreferencesRepository.setRefreshToken(newRefreshToken)
        } else {
            throw Exception("Refresh token is null")
        }
    }

    suspend fun getAccessToken(): String =
        prependBearer(
            userPreferencesRepository.accessTokenFlow.firstOrNull()
                ?: throw IllegalStateException("Access token not available")
        )

    suspend fun linkGETAccount(sessionId: String): Result<Unit> {
        userPreferencesRepository.setSessionId(sessionId)
        val pin = Random.nextInt(10000)
        userPreferencesRepository.setPin(pin)
        return tryRequestWithResult {
            networkApi.authorizeUser(
                loginRequest = LoginRequest(pin.toString(), sessionId)
            )
        }
    }

    suspend fun refreshLogin(pin: Int): Result<Unit> = tryRequestWithResult {
        val newSessionId = networkApi.refreshAuthorizedUser(
            loginPIN = LoginPIN(pin.toString())
        ).sessionId
        if (newSessionId == null) {
            throw Exception("Session ID is null")
        } else {
            userPreferencesRepository.setSessionId(newSessionId)
        }
    }

    suspend fun getSessionId(): String = userPreferencesRepository.sessionIdFlow.firstOrNull() ?: ""

    suspend fun getPin(): Int = userPreferencesRepository.pinFlow.firstOrNull() ?: 0

    suspend fun clearSessionId() = userPreferencesRepository.setSessionId("")

    /**
     * Converts exceptions into appropriate [NetworkError] types.
     */
    private fun handleException(e: Exception): NetworkError = when (e) {
        is HttpException -> when (e.code()) {
            401, 403 -> NetworkError.Unauthorized
            in 400..599 -> NetworkError.ServerError(e.code(), e.message())
            else -> NetworkError.Unknown(e)
        }

        is SocketTimeoutException -> NetworkError.Timeout
        is IOException -> NetworkError.NetworkFailure
        else -> NetworkError.Unknown(e)
    }

    /**
     * Safely executes a network request and wraps the result in a [Result] object.
     */
    private suspend fun <T> safeRequest(request: suspend () -> T): Result<T> {
        return try {
            Result.Success(request())
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    /**
     * Tries to make the given request, and if it fails, refreshes tokens and tries again.
     * Returns a [Result] wrapping the response or error.
     */
    private suspend fun <T> tryRequestWithResult(request: suspend () -> T): Result<T> {
        return try {
            Result.Success(request())
        } catch (_: Exception) {
            try {
                refreshTokens()
                Result.Success(request())
            } catch (retryException: Exception) {
                Result.Error(handleException(retryException))
            }
        }
    }

    private fun prependBearer(str: String) = "Bearer $str"
}

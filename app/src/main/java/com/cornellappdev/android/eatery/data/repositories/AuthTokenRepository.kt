package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.DeviceId
import com.cornellappdev.android.eatery.data.models.RefreshRequest
import com.cornellappdev.android.eatery.data.models.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

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
    suspend fun getTokens(): Result<Unit> = safeNetworkRequest {
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

    suspend fun refreshTokens(): Result<Unit> = safeNetworkRequest {
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


    private fun prependBearer(str: String) = "Bearer $str"
}

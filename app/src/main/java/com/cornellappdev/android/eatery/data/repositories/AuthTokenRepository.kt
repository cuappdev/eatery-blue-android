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
     * A [StateFlow] that emits whether the app has successfully stored a valid token pair.
     */
    val tokensConfiguredFlow: StateFlow<Boolean> = _tokensConfiguredFlow.asStateFlow()

    /**
     * Fetches the initial token pair from the API using the device ID.
     * Called on app launch.
     */
    suspend fun getTokens(): Result<Unit> = resultOfNetworkCall {
        val deviceId = userPreferencesRepository.getOrCreateDeviceId()
        val tokenResponse = networkApi.verifyToken(DeviceId(deviceId))
        val accessToken = tokenResponse.accessToken
            ?: throw IllegalStateException("verifyToken returned null access token")
        val refreshToken = tokenResponse.refreshToken
            ?: throw IllegalStateException("verifyToken returned null refresh token")
        storeTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun markTokensAsConfigured() {
        _tokensConfiguredFlow.value = true
    }

    /**
     * Refreshes the current token pair using the stored refresh token.
     */
    suspend fun refreshTokens(): Result<Unit> = resultOfNetworkCall {
        val deviceId = userPreferencesRepository.getOrCreateDeviceId()
        val refreshToken = userPreferencesRepository.refreshTokenFlow.firstOrNull()
            ?: throw IllegalStateException("Refresh token not available")
        val refreshedTokenResponse = networkApi.refreshToken(
            RefreshRequest(
                deviceId = deviceId,
                refreshToken = refreshToken
            )
        )
        val accessToken = refreshedTokenResponse.accessToken
            ?: throw IllegalStateException("refreshToken returned null access token")
        val refreshTokenFromResponse = refreshedTokenResponse.refreshToken
            ?: throw IllegalStateException("refreshToken returned null refresh token")
        storeTokens(
            accessToken = accessToken,
            refreshToken = refreshTokenFromResponse
        )
    }

    suspend fun getAccessToken(): String =
        "Bearer ${
            userPreferencesRepository.accessTokenFlow.firstOrNull()
                ?: throw IllegalStateException("Access token not available")
        }"

    private suspend fun storeTokens(accessToken: String, refreshToken: String) {
        userPreferencesRepository.setAccessToken(accessToken)
        userPreferencesRepository.setRefreshToken(refreshToken)
    }
}

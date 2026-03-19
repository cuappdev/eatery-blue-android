package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.LoginPIN
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.Result
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GetAccountRepository @Inject constructor(
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authTokenRepository: AuthTokenRepository
) {
    suspend fun linkGETAccount(sessionId: String): Result<Unit> {
        userPreferencesRepository.setSessionId(sessionId)
        val pin = Random.nextInt(10000)
        userPreferencesRepository.setPin(pin)
        return tryRequestWithResult {
            networkApi.authorizeUser(
                loginRequest = LoginRequest(pin.toString(), sessionId)
            )
        }.also { result ->
            if (result is Result.Success) {
                setIsLoggedIn(true)
            }
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

    suspend fun setIsLoggedIn(isLoggedIn: Boolean) =
        userPreferencesRepository.setIsLoggedIn(isLoggedIn)

    suspend fun isLoggedIn(): Boolean =
        userPreferencesRepository.isLoggedInFlow.firstOrNull() ?: false

    private suspend fun <T> tryRequestWithResult(request: suspend () -> T): Result<T> =
        tryRequestWithTokenRefresh(
            request = request,
            refreshTokens = authTokenRepository::refreshTokens
        )
}



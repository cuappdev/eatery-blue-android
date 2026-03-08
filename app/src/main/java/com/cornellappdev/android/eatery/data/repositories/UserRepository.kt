package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.DeviceId
import com.cornellappdev.android.eatery.data.models.FavoriteEatery
import com.cornellappdev.android.eatery.data.models.FavoriteItem
import com.cornellappdev.android.eatery.data.models.Financials
import com.cornellappdev.android.eatery.data.models.LoginPIN
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.RefreshRequest
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.SessionID
import com.cornellappdev.android.eatery.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class UserRepository @Inject constructor(
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val _loadedUser: MutableStateFlow<User?> = MutableStateFlow(null)

    /**
     * The currently loaded user. Null if no user is logged in.
     */
    val loadedUser: StateFlow<User?> = _loadedUser.asStateFlow()

    private val _favoritesEateriesFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())

    /**
     * A [StateFlow] emitting a list of the names of the user's favorite eateries.
     */
    val favoriteEateriesFlow: StateFlow<List<String>> = _favoritesEateriesFlow.asStateFlow()
    private val _favoriteItemsFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())

    /**
     * A [StateFlow] emitting a list of the names of the user's favorite menu items.     */
    val favoriteItemsFlow: StateFlow<List<String>> = _favoriteItemsFlow.asStateFlow()

    private val _tokensConfiguredFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * A [StateFlow] that emits whether configureTokens() has completed successfully.
     */
    val tokensConfiguredFlow: StateFlow<Boolean> = _tokensConfiguredFlow.asStateFlow()

    private val useLocalFavorites = BuildConfig.USE_LOCAL_FAVORITES

    suspend fun getDeviceId(): String {
        val deviceId = userPreferencesRepository.getDeviceId()
        if (deviceId != null) return deviceId

        // first launch
        val uuid = UUID.randomUUID()
        userPreferencesRepository.setDeviceId(uuid)
        return uuid.toString()
    }

    // called on app launch
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

    suspend fun updateFavorites(): Result<Unit> {
        if (useLocalFavorites) {
            _favoritesEateriesFlow.value = userPreferencesRepository.getFavoriteEateryNames()
            _favoriteItemsFlow.value = userPreferencesRepository.getFavoriteItemNames()
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            val accessPhrase = getAccessToken()
            val matches = networkApi.getFavoriteMatches(accessToken = accessPhrase)
            _favoritesEateriesFlow.value = matches.mapNotNull { it.eateryName }
            _favoriteItemsFlow.value = run {
                val items: MutableList<String> = mutableListOf()
                matches.forEach { (_, eateryItems) ->
                    if (eateryItems != null) {
                        items.addAll(eateryItems.mapNotNull { it.name })
                    }
                }
                items.toList()
            }
        }
    }

    suspend fun sendReport(issue: String, report: String, eateryID: Int?): Result<Any> =
        tryRequestWithResult {
            networkApi.sendReport(
                report = ReportSendBody(
                    eatery = eateryID,
                    content = "$issue: $report"
                )
            )
        }

    suspend fun addFavoriteItem(name: String): Result<Unit> {
        if (useLocalFavorites) {
            userPreferencesRepository.setFavoriteItemName(name, true)
            _favoriteItemsFlow.update { currentItems ->
                if (name !in currentItems) currentItems + name else currentItems
            }
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            networkApi.addFavoriteItem(
                accessToken = getAccessToken(),
                item = FavoriteItem(item = name)
            )
            _favoriteItemsFlow.update { currentItems ->
                if (name !in currentItems) currentItems + name else currentItems
            }
        }
    }

    suspend fun removeFavoriteItem(name: String): Result<Unit> {
        if (useLocalFavorites) {
            userPreferencesRepository.setFavoriteItemName(name, false)
            _favoriteItemsFlow.update { currentItems ->
                currentItems.filter { it != name }
            }
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            networkApi.deleteFavoriteItem(
                accessToken = getAccessToken(),
                item = FavoriteItem(name)
            )
            _favoriteItemsFlow.update { currentItems ->
                currentItems.filter { it != name }
            }
        }
    }

    suspend fun addFavoriteEatery(id: Int, eateryName: String): Result<Unit> {
        if (useLocalFavorites) {
            userPreferencesRepository.setFavoriteEateryName(eateryName, true)
            _favoritesEateriesFlow.update { currentEateries ->
                if (eateryName !in currentEateries) currentEateries + eateryName else currentEateries
            }
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            networkApi.addFavoriteEatery(
                accessToken = getAccessToken(),
                eatery = FavoriteEatery(id),
            )
            _favoritesEateriesFlow.update { currentEateries ->
                if (eateryName !in currentEateries) currentEateries + eateryName else currentEateries
            }
        }
    }

    suspend fun removeFavoriteEatery(id: Int, eateryName: String): Result<Unit> {
        if (useLocalFavorites) {
            userPreferencesRepository.setFavoriteEateryName(eateryName, false)
            _favoritesEateriesFlow.update { currentEateries ->
                currentEateries.filter { it != eateryName }
            }
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            networkApi.deleteFavoriteEatery(
                accessToken = getAccessToken(),
                eatery = FavoriteEatery(id)
            )
            _favoritesEateriesFlow.update { currentEateries ->
                currentEateries.filter { it != eateryName }
            }
        }
    }

    suspend fun linkGETAccount(sessionId: String): Result<Unit> {
        userPreferencesRepository.setSessionId(sessionId)
        val pin = Random.nextInt(10000)
        userPreferencesRepository.setPin(pin)
        return tryRequestWithResult {
            networkApi.authorizeUser(
                accessToken = getAccessToken(),
                loginRequest = LoginRequest(pin.toString(), sessionId)
            )
        }
    }

    suspend fun getFinancials(): Result<Financials> = tryRequestWithResult {
        var financials: Financials
        try {
            financials = networkApi.getFinancials(
                accessToken = getAccessToken(),
                sessionId = SessionID(userPreferencesRepository.getSessionId())
            )
        } catch (_: Exception) {
            val pin = userPreferencesRepository.getPin()
            refreshLogin(pin = pin)
            financials = networkApi.getFinancials(
                accessToken = getAccessToken(),
                sessionId = SessionID(userPreferencesRepository.getSessionId())
            )
        }
        _loadedUser.value = User(
            brbBalance = financials.accounts?.brbBalance?.balance,
            cityBucksBalance = financials.accounts?.cityBucksBalance?.balance,
            laundryBalance = financials.accounts?.laundryBalance?.balance,
            transactions = financials.transactions,
//            mealSwipes = financials.accounts?.mealSwipes
        )
        financials
    }

    suspend fun setIsLoggedIn(isLoggedIn: Boolean) =
        userPreferencesRepository.setIsLoggedIn(isLoggedIn)

    suspend fun isLoggedIn(): Boolean = userPreferencesRepository.getIsLoggedIn()

    /**
     * Refreshes GET sessionID.
     */
    suspend fun refreshLogin(pin: Int): Result<Unit> = tryRequestWithResult {
        val newSessionId = networkApi.refreshAuthorizedUser(
            accessToken = getAccessToken(),
            loginPIN = LoginPIN(pin.toString())
        ).sessionId
        if (newSessionId == null) {
            throw Exception("Session ID is null")
        } else {
            userPreferencesRepository.setSessionId(newSessionId)
        }
    }

    suspend fun logout() {
        _loadedUser.value = null
        userPreferencesRepository.setSessionId("")
        userPreferencesRepository.setIsLoggedIn(false)
    }

    suspend fun hasOnboarded(): Boolean = userPreferencesRepository.getHasOnboarded()

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

    /**
     * Gets refresh token assuming device has been registered
     */
    private suspend fun refreshTokens() {
        val deviceId = getDeviceId()
        val refreshToken = userPreferencesRepository.getRefreshToken()
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

    /**
     * Gets access token with Bearer prefix assuming device has been registered
     */
    private suspend fun getAccessToken(): String =
        prependBearer(
            userPreferencesRepository.getAccessToken()
                ?: throw IllegalStateException("Access token not available")
        )

    private fun prependBearer(str: String) = "Bearer $str"
}
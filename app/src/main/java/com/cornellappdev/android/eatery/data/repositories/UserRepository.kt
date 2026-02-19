package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.DeviceId
import com.cornellappdev.android.eatery.data.models.FavoriteEatery
import com.cornellappdev.android.eatery.data.models.FavoriteItem
import com.cornellappdev.android.eatery.data.models.Financials
import com.cornellappdev.android.eatery.data.models.LoginPIN
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.RefreshRequest
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class UserRepository @Inject constructor(
    private val networkApi: NetworkApi,
    val userPreferencesRepository: UserPreferencesRepository
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
     * A [StateFlow] emitting a map from menu items to whether they are favorited.
     */
    val favoriteItemsFlow: StateFlow<List<String>> = _favoriteItemsFlow.asStateFlow()

    suspend fun hasLaunchedBefore(): Boolean = userPreferencesRepository.getDeviceId() != null

    suspend fun getDeviceId(): String {
        val deviceId = userPreferencesRepository.getDeviceId()
        if (deviceId != null) return deviceId

        // first launch
        val uuid = UUID.randomUUID()
        userPreferencesRepository.setDeviceId(uuid)
        return uuid.toString()
    }

    // called on first app launch
    suspend fun registerDevice() {
        val deviceId = UUID.randomUUID()
        userPreferencesRepository.setDeviceId(deviceId)
    }

    // called on app launch
    suspend fun getTokens() {
        val deviceId =
            userPreferencesRepository.getDeviceId() ?: throw Exception("Device not registered")
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

    suspend fun updateFavorites() {
        val accessPhrase = getAccessToken()
        val favoritesResponse = tryRequest {
            networkApi.getFavoriteMatches(accessToken = accessPhrase)
        }
        val matches = favoritesResponse.matches ?: return
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

    suspend fun sendReport(issue: String, report: String, eateryID: Int?): Any =
        tryRequest {
            networkApi.sendReport(
                report = ReportSendBody(
                    eatery = eateryID,
                    content = "$issue: $report"
                )
            )
        }

    suspend fun addFavoriteItem(name: String) = tryRequest {
        networkApi.addFavoriteItem(
            accessToken = getAccessToken(),
            item = FavoriteItem(item = name)
        )
    }

    suspend fun removeFavoriteItem(name: String) = tryRequest {
        networkApi.deleteFavoriteItem(
            accessToken = getAccessToken(),
            item = FavoriteItem(name)
        )
    }

    suspend fun addFavoriteEatery(id: Int) = tryRequest {
        networkApi.addFavoriteEatery(
            accessToken = getAccessToken(),
            eatery = FavoriteEatery(id),
        )
    }

    suspend fun removeFavoriteEatery(id: Int) = tryRequest {
        networkApi.deleteFavoriteEatery(
            accessToken = getAccessToken(),
            eatery = FavoriteEatery(id)
        )
    }

    suspend fun linkGETAccount(sessionId: String) {
        userPreferencesRepository.setSessionId(sessionId)
        val pin = Random.nextInt(10000)
        userPreferencesRepository.setPin(pin)
        tryRequest {
            networkApi.authorizeUser(
                accessToken = getAccessToken(),
                loginRequest = LoginRequest(pin, sessionId)
            )
        }
    }

    suspend fun getFinancials(): Financials = tryRequest {
        var financials: Financials
        try {
            financials = networkApi.getFinancials(
                accessToken = getAccessToken()
            )
        } catch (_: Exception) {
            val pin =
                userPreferencesRepository.getPin()
            refreshLogin(pin = pin)
            financials = networkApi.getFinancials(accessToken = getAccessToken())
        }
        financials
    }

    suspend fun isLoggedIn(): Boolean = userPreferencesRepository.getIsLoggedIn()

    /**
     * Refreshes GET sessionID and returns it.
     */
    suspend fun refreshLogin(pin: Int) = tryRequest {
        val newSessionId = networkApi.refreshAuthorizedUser(
            accessToken = getAccessToken(),
            loginPIN = LoginPIN(pin)
        ).toString()
        userPreferencesRepository.setSessionId(newSessionId)
    }

    suspend fun logout() {
        _loadedUser.value = null
        userPreferencesRepository.setSessionId("")
        userPreferencesRepository.setIsLoggedIn(false)
    }

    suspend fun hasOnboarded(): Boolean = userPreferencesRepository.getHasOnboarded()

    /**
     * Tries to make the given request, and if it fails, refreshes tokens and tries again.
     */
    private suspend fun <T> tryRequest(request: suspend () -> T): T {
        try {
            return request()
        } catch (_: Exception) {
            try {
                refreshTokens()
                return request()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Gets refresh token assuming device has been registered
     */
    private suspend fun refreshTokens() {
        val deviceId = getDeviceId()
        val refreshToken = userPreferencesRepository.getRefreshToken()!!
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
        "Bearer ${userPreferencesRepository.getAccessToken()!!}"

}
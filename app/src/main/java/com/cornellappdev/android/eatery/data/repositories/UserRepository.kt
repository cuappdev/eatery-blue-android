package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.FavoriteEatery
import com.cornellappdev.android.eatery.data.models.FavoriteItem
import com.cornellappdev.android.eatery.data.models.Financials
import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.SessionID
import com.cornellappdev.android.eatery.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authTokenRepository: AuthTokenRepository
) {
    private val _loadedUser: MutableStateFlow<User?> = MutableStateFlow(null)

    /**
     * The currently loaded user. Null if no user is logged in.
     */
    val loadedUser: StateFlow<User?> = _loadedUser.asStateFlow()

    private val _favoriteEateriesFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())

    /**
     * A [StateFlow] emitting a list of the names of the user's favorite eateries.
     */
    val favoriteEateriesFlow: StateFlow<List<String>> = _favoriteEateriesFlow.asStateFlow()
    private val _favoriteItemsFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())

    /**
     * A [StateFlow] emitting a list of the names of the user's favorite menu items.     */
    val favoriteItemsFlow: StateFlow<List<String>> = _favoriteItemsFlow.asStateFlow()

    private val useLocalFavorites = BuildConfig.USE_LOCAL_FAVORITES

    suspend fun updateFavorites(): Result<Unit> {
        if (useLocalFavorites) {
            _favoriteEateriesFlow.value = userPreferencesRepository.favoriteEateryNamesFlow.first()
            _favoriteItemsFlow.value = userPreferencesRepository.favoriteItemNamesFlow.first()
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            val accessPhrase = authTokenRepository.getAccessToken()
            val matches = networkApi.getFavoriteMatches(accessToken = accessPhrase)
            _favoriteEateriesFlow.value = matches.mapNotNull { it.eateryName }
            _favoriteItemsFlow.value = run {
                val items: List<String> =
                    matches.flatMap { it.items.orEmpty() }.mapNotNull { it.name }
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
                accessToken = authTokenRepository.getAccessToken(),
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
                accessToken = authTokenRepository.getAccessToken(),
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
            _favoriteEateriesFlow.update { currentEateries ->
                if (eateryName !in currentEateries) currentEateries + eateryName else currentEateries
            }
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            networkApi.addFavoriteEatery(
                accessToken = authTokenRepository.getAccessToken(),
                eatery = FavoriteEatery(id),
            )
            _favoriteEateriesFlow.update { currentEateries ->
                if (eateryName !in currentEateries) currentEateries + eateryName else currentEateries
            }
        }
    }

    suspend fun removeFavoriteEatery(id: Int, eateryName: String): Result<Unit> {
        if (useLocalFavorites) {
            userPreferencesRepository.setFavoriteEateryName(eateryName, false)
            _favoriteEateriesFlow.update { currentEateries ->
                currentEateries.filter { it != eateryName }
            }
            return Result.Success(Unit)
        }

        return tryRequestWithResult {
            networkApi.deleteFavoriteEatery(
                accessToken = authTokenRepository.getAccessToken(),
                eatery = FavoriteEatery(id)
            )
            _favoriteEateriesFlow.update { currentEateries ->
                currentEateries.filter { it != eateryName }
            }
        }
    }


    suspend fun getFinancials(): Result<Financials> = tryRequestWithResult {
        var financials: Financials
        try {
            financials = networkApi.getFinancials(
                accessToken = authTokenRepository.getAccessToken(),
                sessionId = SessionID(authTokenRepository.getSessionId())
            )
        } catch (_: Exception) {
            val pin = authTokenRepository.getPin()
            authTokenRepository.refreshLogin(pin = pin)
            financials = networkApi.getFinancials(
                accessToken = authTokenRepository.getAccessToken(),
                sessionId = SessionID(authTokenRepository.getSessionId())
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

    suspend fun isLoggedIn(): Boolean = userPreferencesRepository.isLoggedInFlow.first()

    suspend fun logout() {
        _loadedUser.value = null
        authTokenRepository.clearAuthTokens()
        userPreferencesRepository.setIsLoggedIn(false)
    }

    suspend fun hasOnboarded(): Boolean = userPreferencesRepository.hasOnboardedFlow.first()

    /**
     * Tries to make the given request, and if it fails, refreshes tokens and tries again.
     * Returns a [Result] wrapping the response or error.
     */
    private suspend fun <T> tryRequestWithResult(request: suspend () -> T): Result<T> {
        return try {
            Result.Success(request())
        } catch (_: Exception) {
            try {
                authTokenRepository.refreshTokens()
                Result.Success(request())
            } catch (retryException: Exception) {
                Result.Error(handleException(retryException))
            }
        }
    }

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
}
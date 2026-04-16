package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.FavoriteEatery
import com.cornellappdev.android.eatery.data.models.FavoriteItem
import com.cornellappdev.android.eatery.data.models.Financials
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.SessionID
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.User
import com.cornellappdev.android.eatery.data.models.toTransactionAccountType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getAccountRepository: GETAccountRepository
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
            _favoriteEateriesFlow.value =
                userPreferencesRepository.favoriteEateryNamesFlow.firstOrNull() ?: emptyList()
            _favoriteItemsFlow.value =
                userPreferencesRepository.favoriteItemNamesFlow.firstOrNull() ?: emptyList()
            return Result.Success(Unit)
        }

        return resultOfNetworkCall {
            val matches = networkApi.getFavoriteMatches()
            _favoriteEateriesFlow.value = matches.mapNotNull { it.eateryName }
            _favoriteItemsFlow.value = run {
                val items: List<String> =
                    matches.flatMap { it.items.orEmpty() }.mapNotNull { it?.name }
                items.toList()
            }
        }
    }

    suspend fun sendReport(issue: String, report: String, eateryID: Int?): Result<Any> =
        resultOfNetworkCall {
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

        return resultOfNetworkCall {
            networkApi.addFavoriteItem(
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

        return resultOfNetworkCall {
            networkApi.deleteFavoriteItem(
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

        return resultOfNetworkCall {
            networkApi.addFavoriteEatery(
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

        return resultOfNetworkCall {
            networkApi.deleteFavoriteEatery(
                eatery = FavoriteEatery(id)
            )
            _favoriteEateriesFlow.update { currentEateries ->
                currentEateries.filter { it != eateryName }
            }
        }
    }


    suspend fun getFinancials(): Result<Financials> = resultOfNetworkCall {
        var financials: Financials
        try {
            financials = networkApi.getFinancials(
                sessionId = SessionID(getAccountRepository.getSessionId())
            )
        } catch (_: Exception) {
            val pin = getAccountRepository.getPin() ?: throw IllegalStateException()
            getAccountRepository.refreshLogin(pin = pin)
            financials = networkApi.getFinancials(
                sessionId = SessionID(getAccountRepository.getSessionId())
            )
        }

        _loadedUser.value = User(
            brbBalance = financials.accounts?.brbBalance?.balance ?: 0.0,
            cityBucksBalance = financials.accounts?.cityBucksBalance?.balance ?: 0.0,
            laundryBalance = financials.accounts?.laundryBalance?.balance ?: 0.0,
            transactions = financials.transactions?.filterNotNull()
                ?.mapNotNull { transaction ->
                    val date = transaction.date?.toLocalDateTime()
                    if (transaction.amount == null ||
                        transaction.accountType == null ||
                        date == null ||
                        transaction.location == null
                    ) return@mapNotNull null
                    Transaction(
                        amount = transaction.amount,
                        accountType = transaction.accountType.toTransactionAccountType(),
                        date = date,
                        location = transaction.location
                    )
                } ?: emptyList()
//            mealSwipes = financials.accounts?.mealSwipes
        )
        financials
    }

    private fun String.toLocalDateTime(): LocalDateTime? {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        return runCatching {
            ZonedDateTime
                .parse(this, inputFormatter)
                .withZoneSameInstant(java.time.ZoneId.systemDefault())
                .toLocalDateTime()
        }.getOrNull()
    }

    suspend fun logout() {
        _loadedUser.value = null
        getAccountRepository.clearSessionId()
        getAccountRepository.setIsLoggedIn(false)
    }

    suspend fun hasOnboarded(): Boolean =
        userPreferencesRepository.hasOnboardedFlow.firstOrNull() ?: false
}
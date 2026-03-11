package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.data.models.User
import com.cornellappdev.android.eatery.data.models.toTransactionAccountType
import com.cornellappdev.android.eatery.data.repositories.AuthTokenRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TransactionWithFormattedDate(
    val transaction: Transaction,
    val formattedDate: String
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authTokenRepository: AuthTokenRepository,
) : ViewModel() {

    /**
     * State class contains the two classes that will be passed down through the flow to the login, profile, and account related views.
     */
    sealed class State {
        data class Login(
            val netID: String = "",
            val password: String = "",
            val loading: Boolean = false
        ) : State()

        data class Account(
            val user: User, // Contains all user data.
            var query: String, // Search bar query.
            var accountFilter: TransactionAccountType
        ) : State()

        fun getBalances(): AccountBalances {
            if (this !is Account) return AccountBalances()
            return AccountBalances(
                brbBalance = this.user.brbBalance,
                cityBucksBalance = this.user.cityBucksBalance,
                laundryBalance = this.user.laundryBalance,
                mealSwipes = this.user.mealSwipes
            )
        }
    }

    private val _queryFlow = MutableStateFlow("")

    fun setQuery(query: String) {
        _queryFlow.value = query
    }

    private val _accountTypeFilterFlow = MutableStateFlow(TransactionAccountType.BRBS)

    fun updateAccountFilter(newAccountType: TransactionAccountType) {
        _accountTypeFilterFlow.value = newAccountType
    }

    private val _loginLoadingFlow = MutableStateFlow(false)

    val state: Flow<State> = combine(
        userRepository.loadedUser,
        _queryFlow,
        _accountTypeFilterFlow,
        _loginLoadingFlow
    ) { loadedUser, query, accountFilter, loginLoading ->
        if (loadedUser != null) {
            State.Account(
                user = loadedUser,
                query = query,
                accountFilter = accountFilter
            )
        } else {
            State.Login(
                netID = "",
                password = "",
                loading = loginLoading
            )
        }
    }

    private val _error = MutableStateFlow<NetworkUiError?>(null)
    val error = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    init {
        viewModelScope.launch {
            if (userRepository.isLoggedIn()) {
                getFinancials()
            }
        }
    }


    val filteredTransactionsFlow: Flow<List<TransactionWithFormattedDate>> =
        combine(
            userRepository.loadedUser,
            _queryFlow,
            _accountTypeFilterFlow
        ) { loadedUser, query, accountType ->
            if (loadedUser == null) return@combine emptyList()
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            loadedUser.transactions?.filter { transaction ->
                val matchesAccountType =
                    transaction.accountType.toTransactionAccountType() == accountType
                val pastThirtyDays = LocalDateTime.parse(
                    transaction.date,
                    inputFormatter
                ) >= LocalDateTime.now().minusDays(30)
                val matchesQuery = transaction.location.lowercase().contains(query.lowercase())
                matchesAccountType && pastThirtyDays && matchesQuery
            }?.map { transaction ->
                TransactionWithFormattedDate(
                    transaction = transaction,
                    formattedDate = formatDate(transaction.date)
                )
            } ?: emptyList()
        }

    companion object {
        fun formatDate(dateString: String): String {
            return try {
                // Parse timezone-aware string like "2026-03-02T01:56:45.000+0000"
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                val zonedDateTime = java.time.ZonedDateTime.parse(dateString, inputFormatter)

                // Convert to system's local timezone
                val localZonedDateTime =
                    zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
                val localDateTime = localZonedDateTime.toLocalDateTime()

                val outputFormatter = DateTimeFormatter.ofPattern("h:mm a · EEEE, MMMM d")
                outputFormatter.format(localDateTime)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    fun onLoginPressed() = updateLoginLoadingState(true)

    fun onLoginExited() = updateLoginLoadingState(false)

    private fun updateLoginLoadingState(isLoading: Boolean) {
        _loginLoadingFlow.value = isLoading
    }

    fun onLoginWebViewSuccess(sessionId: String) {
        viewModelScope.launch {
            if (linkGETAccount(sessionId)) {
                getFinancials()
            }
        }
    }

    /**
     * Fetches user data given [sessionId] and updates the state and user preferences.
     * Returns true if the account was linked successfully, false otherwise.
     */
    private suspend fun linkGETAccount(sessionId: String): Boolean {
        return when (val result = authTokenRepository.linkGETAccount(sessionId)) {
            is Result.Success -> {
                userRepository.setIsLoggedIn(true)
                _error.value = null
                true
            }

            is Result.Error -> {
                _error.value = NetworkUiError.Failed(NetworkAction.LinkGetAccount, result.error)
                updateLoginLoadingState(false)
                false
            }
        }
    }

    private suspend fun getFinancials() {
        when (val result = userRepository.getFinancials()) {
            is Result.Success -> {
                _error.value = null
            }

            is Result.Error -> {
                _error.value = NetworkUiError.Failed(NetworkAction.GetFinancials, result.error)
                updateLoginLoadingState(false)
            }
        }
    }
}

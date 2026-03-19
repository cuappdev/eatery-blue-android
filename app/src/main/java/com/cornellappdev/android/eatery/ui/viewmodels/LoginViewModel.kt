package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.data.models.User
import com.cornellappdev.android.eatery.data.repositories.GetAccountRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.DisplayTransaction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getAccountRepository: GetAccountRepository,
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
            val query: String, // Search bar query.
            val accountFilter: TransactionAccountType
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
    private val _accountTypeFilterFlow = MutableStateFlow(TransactionAccountType.BRBS)
    private val _loginLoadingFlow = MutableStateFlow(false)

    val state: StateFlow<State> = combine(
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = State.Login()
    )

    private val _error = MutableStateFlow<NetworkUiError?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            if (getAccountRepository.isLoggedIn()) {
                getFinancials()
            }
        }
    }

    val filteredTransactionsFlow: StateFlow<List<DisplayTransaction>> =
        combine(
            userRepository.loadedUser,
            _queryFlow,
            _accountTypeFilterFlow
        ) { loadedUser, query, accountFilter ->
            if (loadedUser == null) return@combine emptyList()
            loadedUser.transactions.filter {
                it.location.lowercase().contains(query.lowercase())
                        && it.accountType == accountFilter
                        && it.date >= LocalDateTime.now().minusDays(30)
            }.map { it.toDisplayTransaction() }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    companion object {
        private fun LocalDateTime.formatDate(): String {
            val outputFormatter = DateTimeFormatter.ofPattern("h:mm a · EEEE, MMMM d")
            return outputFormatter.format(this)
        }

        private fun Transaction.toDisplayTransaction(): DisplayTransaction = DisplayTransaction(
            id = listOf(
                this.date,
                this.location,
                this.amount.toString(),
                this.accountType.name
            ).joinToString("|"),
            amount = this.amount,
            accountType = this.accountType,
            location = this.location,
            formattedDate = this.date.formatDate()
        )
    }

    fun setQuery(query: String) {
        _queryFlow.value = query
    }

    fun updateAccountFilter(newAccountType: TransactionAccountType) {
        _accountTypeFilterFlow.value = newAccountType
    }

    fun clearError() {
        _error.value = null
    }

    fun onLoginPressed() {
        _loginLoadingFlow.value = true
    }

    fun onLoginExited() {
        _loginLoadingFlow.value = false
    }

    fun onLoginWebViewSuccess(sessionId: String) {
        viewModelScope.launch {
            if (linkGETAccount(sessionId)) {
                getFinancials()
            }
        }
    }

    private fun onNetworkSuccess() {
        _error.value = null
    }

    private fun onNetworkFailure(action: NetworkAction, error: NetworkError) {
        _error.value = NetworkUiError.Failed(action, error)
        _loginLoadingFlow.value = false
    }

    /**
     * Fetches user data given [sessionId] and updates the state and user preferences.
     * Returns true if the account was linked successfully, false otherwise.
     */
    private suspend fun linkGETAccount(sessionId: String): Boolean {
        return when (val result = getAccountRepository.linkGETAccount(sessionId)) {
            is Result.Success -> {
                onNetworkSuccess()
                true
            }

            is Result.Error -> {
                onNetworkFailure(NetworkAction.LinkGetAccount, result.error)
                false
            }
        }
    }

    private suspend fun getFinancials() {
        when (val result = userRepository.getFinancials()) {
            is Result.Success -> onNetworkSuccess()
            is Result.Error -> onNetworkFailure(NetworkAction.GetFinancials, result.error)
        }
    }
}

package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.data.models.User
import com.cornellappdev.android.eatery.data.repositories.GETAccountRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.DisplayTransaction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getAccountRepository: GETAccountRepository,
) : ViewModel() {

    data class ProfileUiState(
        val isLoginState: Boolean = true,
        val isLoading: Boolean = false,
        val accountTypeBalance: AccountBalances = AccountBalances(),
        val accountFilter: TransactionAccountType = TransactionAccountType.BRBS,
        val filterText: String = "",
        val filteredTransactions: List<DisplayTransaction> = emptyList(),
        val error: NetworkUiError? = null
    )

    private val _queryFlow = MutableStateFlow("")
    private val _accountTypeFilterFlow = MutableStateFlow(TransactionAccountType.BRBS)
    private val _isLoginLoadingFlow = MutableStateFlow(false)
    private val _error = MutableStateFlow<NetworkUiError?>(null)

    /**
     * Whether financials data of previously logged-in user is being fetched.
     * Should show Account page if true.
     */
    private val _isRestoringSessionFlow = MutableStateFlow(false)

    private val loginDataState: Flow<ProfileUiState> = combine(
        userRepository.loadedUser,
        _queryFlow,
        _accountTypeFilterFlow,
        _isRestoringSessionFlow,
        _isLoginLoadingFlow,
    ) { loadedUser, query, accountFilter, isRestoringSession, loginLoading ->
        val isLoginState = loadedUser == null && !isRestoringSession
        val lowercaseQuery = query.lowercase()
        val thirtyDaysAgo = LocalDateTime.now().minusDays(30)
        val filteredTransactions = loadedUser?.transactions?.filter {
            it.location.lowercase().contains(lowercaseQuery)
                    && it.accountType == accountFilter
                    && it.date >= thirtyDaysAgo
        }?.map { it.toDisplayTransaction() } ?: emptyList()

        ProfileUiState(
            isLoginState = isLoginState,
            isLoading = loginLoading || isRestoringSession,
            accountTypeBalance = loadedUser?.toAccountBalances() ?: AccountBalances(),
            accountFilter = if (isLoginState) TransactionAccountType.BRBS else accountFilter,
            filterText = if (isLoginState) "" else query,
            filteredTransactions = filteredTransactions,
        )
    }

    val uiState = combine(loginDataState, _error) { dataState, error ->
        dataState.copy(error = error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    init {
        viewModelScope.launch {
            if (getAccountRepository.isLoggedIn()) {
                _isRestoringSessionFlow.value = true
                try {
                    getFinancials()
                } finally {
                    _isRestoringSessionFlow.value = false
                }
            }
        }
    }

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

        private fun User.toAccountBalances(): AccountBalances {
            return AccountBalances(
                brbBalance = this.brbBalance,
                cityBucksBalance = this.cityBucksBalance,
                laundryBalance = this.laundryBalance,
                mealSwipes = this.mealSwipes
            )
        }
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
        _isLoginLoadingFlow.value = true
    }

    fun onLoginExited() {
        _isLoginLoadingFlow.value = false
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
        _isLoginLoadingFlow.value = false
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

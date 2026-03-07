package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.data.models.User
import com.cornellappdev.android.eatery.data.models.toTransactionAccountType
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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    /**
     * State class contains the two classes that will be passed down through the flow to the login, profile, and account related views.
     */
    sealed class State {
        data class Login(
            val netID: String = "",
            val password: String = "",
            val failure: NetworkUiError? = null,
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

    private var _state = MutableStateFlow<State>(
        userRepository.loadedUser.value
            ?.let {
                State.Account(
                    user = it,
                    query = "",
                    accountFilter = TransactionAccountType.BRBS
                )
            } ?: State.Login()
    )

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (userRepository.isLoggedIn()) {
                getFinancials()
            }
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

    val filteredTransactionsFlow: Flow<List<Transaction>> =
        combine(_state, _queryFlow, _accountTypeFilterFlow) { state, query, accountType ->
            if (state !is State.Account) return@combine emptyList()
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            state.user.transactions?.filter { transaction ->
                val matchesAccountType =
                    transaction.accountType.toTransactionAccountType() == accountType
                val pastThirtyDays = LocalDateTime.parse(
                    transaction.date,
                    inputFormatter
                ) >= LocalDateTime.now().minusDays(30)
                val matchesQuery = transaction.location.lowercase().contains(query.lowercase())
                matchesAccountType && pastThirtyDays && matchesQuery
            } ?: emptyList()
        }

    fun onLoginPressed() = updateLoginLoadingState(true)

    fun onLoginExited() = updateLoginLoadingState(false)

    private fun updateLoginLoadingState(isLoading: Boolean) {
        val currState = _state.value
        if (currState !is State.Login) return

        // Send the new loading Login state down
        _state.value = currState.copy(loading = isLoading)

    }

    fun onLogoutPressed(onDone: () -> Unit = {}) {
        val newState = State.Login()
        _state.value = newState
        viewModelScope.launch {
            userRepository.logout()
            onDone()
        }
    }

    fun onLoginWebViewSuccess(sessionId: String) {
        viewModelScope.launch {
            linkGETAccount(sessionId)
            getFinancials()
        }
    }

    /**
     * Fetches user data given [sessionId] and updates the state and user preferences.
     */
    private suspend fun linkGETAccount(sessionId: String) {
        when (val result = userRepository.linkGETAccount(sessionId)) {
            is Result.Success -> {
                userRepository.setIsLoggedIn(true)
            }

            is Result.Error -> {
                val currState = _state.value
                if (currState is State.Login) {
                    val newState = currState.copy(
                        failure = NetworkUiError.Failed(NetworkAction.LinkGetAccount, result.error),
                        loading = false
                    )
                    _state.value = newState
                }
            }
        }
    }

    suspend fun getFinancials() {
        when (val result = userRepository.getFinancials()) {
            is Result.Success -> {
                val financials = result.data
                val newState = State.Account(
                    user = User(
                        brbBalance = financials.accounts?.brbBalance?.balance,
                        cityBucksBalance = financials.accounts?.cityBucksBalance?.balance,
                        laundryBalance = financials.accounts?.laundryBalance?.balance,
                        transactions = financials.transactions,
                        // mealSwipes = financials.accounts?.mealSwipes
                    ),
                    query = "",
                    accountFilter = TransactionAccountType.BRBS
                )
                _state.value = newState
            }

            is Result.Error -> {
                val currState = _state.value
                if (currState is State.Login) {
                    val newState = currState.copy(
                        failure = NetworkUiError.Failed(NetworkAction.GetFinancials, result.error),
                        loading = false
                    )
                    _state.value = newState
                }
            }
        }
    }
}

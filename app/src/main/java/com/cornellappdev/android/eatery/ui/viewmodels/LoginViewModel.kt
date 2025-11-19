package com.cornellappdev.android.eatery.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.data.models.User
import com.cornellappdev.android.eatery.data.models.toTransactionAccountType
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    /**
     * State class contains the two classes that will be passed down through the flow to the login, profile, and account related views.
     */
    sealed class State {
        data class Login(
            val netID: String = "",
            val password: String = "",
            val failureMessage: String? = null,
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

    // Convert the state to a flow that can be updated by screens that use the LoginViewModel
    val state = _state.asStateFlow()

    init {
        getSavedLoginInfo()
    }

    fun resetLogin() {
        _state.value = State.Login()
    }

    fun updateAccountFilter(newAccountType: TransactionAccountType) {
        val currState = _state.value
        if (currState !is State.Account) return

        // currState is a Login state (expected).
        val newState = State.Account(
            currState.user,
            "",
            newAccountType
        )

        // Send the new netID Login state down.
        _state.value = newState
    }

    fun getFilteredTransactions(
        accountType: TransactionAccountType,
        query: String
    ): List<Transaction> {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        userRepository.loadedUser.value?.let {
            if (_state.value !is State.Account) return emptyList()
            return it.transactions?.filter { transaction ->
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
        return emptyList()
    }

    fun onLoginPressed() = updateLoginLoadingState(true)

    fun onLoginExited() = updateLoginLoadingState(false)

    private fun updateLoginLoadingState(isLoading: Boolean) {
        val currState = _state.value
        if (currState !is State.Login) return

        // Send the new loading Login state down
        _state.value = currState.copy(loading = isLoading)

    }

    fun onLogoutPressed() {
        val newState = State.Login()
        _state.value = newState
        viewModelScope.launch {
            userRepository.logout()
            userPreferencesRepository.setIsLoggedIn(false)
            userPreferencesRepository.saveLoginInfo("", "")
        }
    }

    private fun getSavedLoginInfo() = viewModelScope.launch {
        if (userPreferencesRepository.getIsLoggedIn()) {
            val loginInfo = userPreferencesRepository.fetchLoginInfo()
            getUser(loginInfo.first)
        }
    }

    fun onLoginWebViewSuccess(sessionId: String) {
        getUser(sessionId)
    }

    /**
     * Fetches user data given [sessionId] and updates the state and user preferences.
     */
    private fun getUser(sessionId: String) = viewModelScope.launch {
        val currState = _state.value
        if (userPreferencesRepository.getDeviceId() == null) {
            userPreferencesRepository.setDeviceId(UUID.randomUUID())
        }
        try {
            val fcmToken =
                com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
            val deviceId = userPreferencesRepository.getDeviceId()!!
            Log.d("debug", "sessionId: $sessionId, deviceId: $deviceId, fcmToken: $fcmToken")
            val user = userRepository.getUser(sessionId, deviceId, fcmToken)
            if (currState is State.Login) {
                userPreferencesRepository.saveLoginInfo(sessionId, currState.password)
                userPreferencesRepository.setIsLoggedIn(true)
            }
            val newState = State.Account(
                user = user,
                query = "",
                accountFilter = TransactionAccountType.BRBS
            )
            _state.value = newState
        } catch (e: Exception) {
            // todo - error state
            val currState = _state.value
            if (currState is State.Login) {
                val newState = State.Login(
                    netID = currState.netID,
                    password = currState.password,
                    failureMessage = e.stackTraceToString(),
                    loading = false
                )
                _state.value = newState
            }
            userPreferencesRepository.saveLoginInfo("", "")
            userPreferencesRepository.setIsLoggedIn(false)
        }
    }
}


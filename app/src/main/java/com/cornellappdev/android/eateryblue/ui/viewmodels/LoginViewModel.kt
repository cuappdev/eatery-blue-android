package com.cornellappdev.android.eateryblue.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.Account
import com.cornellappdev.android.eateryblue.data.models.AccountType
import com.cornellappdev.android.eateryblue.data.models.Transaction
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserRepository
import com.cornellappdev.android.eateryblue.ui.screens.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
            val netid: String,
            val password: String,
            val failureMessage: String?,
            val loading: Boolean
        ) : State()

        data class Account(
            val user: User, // Contains all user data.
            var query: String, // Search bar query.
            var accountFilter: AccountType // Search bar filter.
        ) : State()
    }

    private var _state = MutableStateFlow<State>(
        if (CurrentUser.user == null) {
            State.Login("", "", null, false)
        } else {
            State.Account(CurrentUser.user!!, "", AccountType.BRBS)
        }
    )
    val state = _state.asStateFlow()

    //
    fun checkAccount(accountType: AccountType): Account? {
        if (_state.value !is State.Account || CurrentUser.user == null) return null
        return CurrentUser.user!!.accounts!!.find { account -> account.type == accountType }
    }

    fun updateAccountFilter(newAccountType: AccountType) {
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

    fun getTransactionsOfType(accountType: AccountType, query: String): List<Transaction>? {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        if (_state.value !is State.Account || CurrentUser.user == null) return null
        return CurrentUser.user!!.transactions!!.filter { transaction ->
            transaction.accountType == accountType
                    && LocalDateTime.parse(transaction.date, inputFormatter) >= LocalDateTime.now()
                .minusDays(30)
                    && transaction.location!!.lowercase().contains(query.lowercase())
        }
    }

    fun onNetIDTyped(newNetid: String) {
        val currState = _state.value
        if (currState !is State.Login) return

        // currState is a Login state (expected).
        val loginState = currState
        val newState = State.Login(
            newNetid,
            loginState.password,
            loginState.failureMessage,
            false // Should never be able to type in when loading, anyways.
        )

        // Send the new netID Login state down.
        _state.value = newState
    }

    fun onPasswordTyped(newPassword: String) {
        val currState = _state.value
        if (currState !is State.Login) return

        // currState is a Login state (expected).
        val loginState = currState

        val newState = State.Login(
            loginState.netid,
            newPassword,
            loginState.failureMessage,
            false // Should never be able to type in when loading, anyways.
        )

        // Send the new password Login state down.
        _state.value = newState
    }

    fun onLoginPressed() {
        val currState = _state.value
        if (currState !is State.Login) return

        // currState is a Login state (expected).
        val loginState = currState

        val newState = State.Login(
            loginState.netid,
            loginState.password,
            loginState.failureMessage,
            true // Should never be able to type in when loading, anyways.
        )

        // Send the new loading Login state down
        _state.value = newState
    }

    fun onLoginFailed() {
        val currState = _state.value
        if (currState !is State.Login) return

        val loginState = currState

        val newState = State.Login(
            loginState.netid,
            password = "",
            failureMessage = "",
            false
        )
        _state.value = newState
    }

    fun onLogoutPressed() {
        CurrentUser.user = null
        val currState = _state.value
        if (currState !is State.Account) return

        val newState = State.Login(
            "", "", null, false
        )
        _state.value = newState
    }


    var isLoggedIn: LoggedInStatus by mutableStateOf(LoggedInStatus.Pending)
        private set

    init {
        getSavedLoginInfo()
    }

    fun getSavedLoginInfo() = viewModelScope.launch {
        isLoggedIn = if (userPreferencesRepository.getIsLoggedIn()) {
            val loginInfo = userPreferencesRepository.fetchLoginInfo()
            LoggedInStatus.IsLoggedIn(loginInfo.first, loginInfo.second)
        } else {
            LoggedInStatus.NotLoggedIn
        }
    }

//    fun saveLoginInfo(username: String, password: String) = viewModelScope.launch {
//        userPreferencesRepository.saveLoginInfo(username, password)
//        userPreferencesRepository.setIsLoggedIn(true)
//    }

    fun getUser(sessionId: String) = viewModelScope.launch {
        try {
            val user = userRepository.getUser(sessionId).response!!
            val account = userRepository.getAccount(sessionId, user.id!!).response!!.accounts
            val transactions =
                userRepository.getTransactionHistory(sessionId, user.id).response!!.transactions
            user.accounts = account
            user.transactions = transactions

            CurrentUser.user = user
            val newState = State.Account(
                user = user,
                query = "",
                accountFilter = AccountType.BRBS
            )
            _state.value = newState
        } catch (e: Exception) {
            Log.d("LOGIN", e.stackTraceToString())
            val currState = _state.value
            if (currState is State.Login) {
                val newState = State.Login(
                    netid = currState.netid,
                    password = currState.password,
                    failureMessage = e.stackTraceToString(),
                    loading = false
                )
                _state.value = newState
            }

        }
    }

//    fun logOut() = viewModelScope.launch {
//        CurrentUser.user = null
//        userPreferencesRepository.setIsLoggedIn(false)
//        userPreferencesRepository.saveLoginInfo("", "")
//    }
}

/**
 * A sealed hierarchy describing the current status of logging in.
 */
sealed interface LoginState {
    data class Success(val user: User) : LoginState
    object Error : LoginState
    object Pending : LoginState
}

/**
 * A sealed hierarchy describing the current status of isLoggedIn
 */
sealed interface LoggedInStatus {
    data class IsLoggedIn(val username: String, val password: String) : LoggedInStatus
    object NotLoggedIn : LoggedInStatus
    object Pending : LoggedInStatus
}

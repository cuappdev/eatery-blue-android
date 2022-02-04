package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.Transaction
import com.appdev.eateryblueandroid.models.User
import com.appdev.eateryblueandroid.networking.get.GetApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

class ProfileViewModel: ViewModel() {
    sealed class State {
        object Empty: State()
        data class LoggingIn(val netid: String, val password: String): State()
        data class ProfileData(val user: User, val latestDateFetched: LocalDateTime): State()
        data class LoginFailure(val error: LoginFailureType): State()
    }

    sealed class Display {
        data class Login(val authenticating: Boolean, val progress: Float = 0f): Display()
        object Settings: Display()
        object Profile: Display()
    }

    private var _state = MutableStateFlow<State>(State.Empty)
    val state = _state.asStateFlow()

    private var _display = MutableStateFlow<Display>(Display.Login(authenticating = false))
    val display = _display.asStateFlow()

    fun initiateLogin(netid: String, password: String) {
        _state.value = State.LoggingIn(netid, password)
        _display.value = Display.Login(authenticating = true, progress = 0.3f)
    }

    fun webpageLoaded() {
        _display.value = Display.Login(authenticating = true, progress = 0.6f)
    }

    fun loginSuccess(sessionId: String) {
        _display.value = Display.Login(authenticating = true, progress = 0.9f)
        viewModelScope.launch {
            val res1 = GetApiService.getInstance().fetchUser(
                GetApiService.generateUserBody(sessionId = sessionId)
            )
            if (res1.exception != null || res1.response == null) {
                _state.value = State.LoginFailure(LoginFailureType.FETCH_USER_FAILURE)
                _display.value = Display.Login(authenticating = false)
                return@launch
            }
            _display.value = Display.Login(authenticating = true, progress = 1f)
            val user = res1.response
            val res2 = GetApiService.getInstance().fetchAccounts(
                GetApiService.generateAccountsBody(sessionId = sessionId, userId = user.id ?: "")
            )
            if (res2.exception != null) {
                _state.value = State.LoginFailure(LoginFailureType.FETCH_ACCOUNTS_FAILURE)
                _display.value = Display.Login(authenticating = false)
                return@launch
            }
            val res3 = GetApiService.getInstance().fetchTransactionHistory(
                GetApiService.generateTransactionHistoryBody(
                    endDate = Date(),
                    sessionId = sessionId,
                    userId = user.id ?: ""
                )
            )
            if (res3.exception != null) {
                _state.value = State.LoginFailure(LoginFailureType.FETCH_TRANSACTION_HISTORY_FAILURE)
                _display.value = Display.Login(authenticating = false)
                return@launch
            }
            user.paymentMethods = res2.response?.paymentMethods
            user.transactions = res3.response?.transactions
            _state.value = State.ProfileData(
                user = user,
                latestDateFetched = oldestTransactionDate(user.transactions)
            )
            _display.value = Display.Profile
        }
    }

    fun loginFailure(error: LoginFailureType) {
        _state.value = State.LoginFailure(error)
    }

    internal fun oldestTransactionDate(transactions: List<Transaction>?): LocalDateTime {
        var oldestDate = LocalDateTime.MAX
        if (transactions == null) return oldestDate
        for (transaction in transactions) {
            if (transaction.date?.isBefore(oldestDate) == true) {
                oldestDate = transaction.date
            }
        }
        return oldestDate
    }
}

enum class LoginFailureType {
    USERNAME_PASSWORD_INVALID,
    FETCH_USER_FAILURE,
    FETCH_ACCOUNTS_FAILURE,
    FETCH_TRANSACTION_HISTORY_FAILURE
}
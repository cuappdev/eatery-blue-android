package com.appdev.eateryblueandroid.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.models.User
import com.appdev.eateryblueandroid.networking.get.GetApiService
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.appdev.eateryblueandroid.util.appContext
import com.appdev.eateryblueandroid.util.saveLoginInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel : ViewModel() {
    sealed class State {
        object Empty : State()
        data class LoggingIn(val netid: String, val password: String) : State()
        data class ProfileData(
            val user: User,
            val query: String,
            val accountFilter: AccountType
        ) : State()

        data class LoginFailure(val error: LoginFailureType) : State()
    }

    sealed class Display {
        data class Login(val authenticating: Boolean, val progress: Float = 0f) : Display()
        object Settings : Display()
        object Profile : Display()
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

    fun transitionSettings() {
        _display.value = Display.Settings
    }

    fun transitionProfile() {
        _display.value = Display.Profile
    }

    fun transitionLogin() {
        _display.value = Display.Login(authenticating = false, progress = 0.0f)
    }

    fun logout() {
        _state.value = State.Empty
        saveLoginInfo("", "")
        transitionLogin()
    }

    fun loginSuccess(sessionId: String) {
        _display.value = Display.Login(authenticating = true, progress = 0.9f)
        saveLoginInfo((state.value as State.LoggingIn).netid, (state.value as State.LoggingIn).password)
        viewModelScope.launch {
            val res1 = GetApiService.getInstance().fetchUser(
                GetApiService.generateUserBody(sessionId = sessionId)
            )
            if (res1.exception != null || res1.response == null) {
                loginFailure(LoginFailureType.FETCH_USER_FAILURE)
                return@launch
            }
            _display.value = Display.Login(authenticating = true, progress = 1f)
            val user = res1.response
            val res2 = GetApiService.getInstance().fetchAccounts(
                GetApiService.generateAccountsBody(sessionId = sessionId, userId = user.id ?: "")
            )
            if (res2.exception != null) {
                loginFailure(LoginFailureType.FETCH_ACCOUNTS_FAILURE)
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
                loginFailure(LoginFailureType.FETCH_TRANSACTION_HISTORY_FAILURE)
                return@launch
            }
            user.accounts = res2.response?.accounts
            user.transactions = res3.response?.transactions
            _state.value = State.ProfileData(
                user = user,
                query = "",
                accountFilter = AccountType.MEALPLAN
            )
            _display.value = Display.Profile
        }
    }

    fun loginFailure(error: LoginFailureType) {
        _state.value = State.LoginFailure(error)
        _display.value = Display.Login(authenticating = false)
    }

    fun updateAccountFilter(updatedFilter: AccountType) {
        val currentProfileData = _state.value as? State.ProfileData ?: return
        _state.value = State.ProfileData(
            user = currentProfileData.user,
            query = currentProfileData.query,
            accountFilter = updatedFilter
        )
    }

    fun updateQuery(updatedQuery: String) {
        val currentProfileData = _state.value as? State.ProfileData ?: return
        _state.value = State.ProfileData(
            user = currentProfileData.user,
            query = updatedQuery,
            accountFilter = currentProfileData.accountFilter
        )
    }

}

enum class LoginFailureType {
    USERNAME_PASSWORD_INVALID,
    FETCH_USER_FAILURE,
    FETCH_ACCOUNTS_FAILURE,
    FETCH_TRANSACTION_HISTORY_FAILURE
}
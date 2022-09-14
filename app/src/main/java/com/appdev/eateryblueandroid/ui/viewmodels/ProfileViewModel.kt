package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.models.User
import com.appdev.eateryblueandroid.networking.get.GetApiService
import com.appdev.eateryblueandroid.util.Constants.passwordAlias
import com.appdev.eateryblueandroid.util.LoginRepository
import com.appdev.eateryblueandroid.util.decryptData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class ProfileViewModel : ViewModel() {
    sealed class State {
        object Empty : State()
        data class LoggingIn(val netid: String, val password: String) : State()
        data class AutoLoggingIn(
            val netid: String,
            val password: String,
            val cachedProfileData: ProfileData
        ) : State()

        data class ProfileData(
            val user: User,
            var query: String,
            var accountFilter: AccountType
        ) : State()

        data class LoginFailure(val error: LoginFailureType) : State()
    }

    sealed class Display {
        data class Login(
            val authenticating: Boolean,
            val progress: Float = 0f,
            val intOffset: IntOffset = IntOffset(0, 0)
        ) : Display()

        object Settings : Display()

        object Profile : Display()
        object About : Display()
        object Favorites : Display()
        object Notifications : Display()
        object Privacy : Display()
        object Legal : Display()
        object Support : Display()
        object EateryDetailVisible : Display()
    }

    private var _state = MutableStateFlow<State>(State.Empty)
    val state = _state.asStateFlow()

    private var _display = MutableStateFlow<Display>(Display.Login(authenticating = false))
    val display = _display.asStateFlow()

    fun initiateLogin(netid: String, password: String) {
        _state.value = State.LoggingIn(netid, password)
        _display.value = Display.Login(authenticating = true, progress = 0.3f)
    }

    fun watchForAutoLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            LoginRepository.loginFlow.collect { state ->
                if (state.ready()) {
                    autoLogin(state.username, decryptData(passwordAlias, state.encryptedPassword))
                }
            }
        }
    }

    fun autoLogin(netid: String, password: String) {
        _state.value = State.AutoLoggingIn(
            netid,
            password,
            State.ProfileData(LoginRepository.makeCachedUser(), "", AccountType.MEALSWIPES)
        )
        _display.value = Display.Login(authenticating = true, progress = 0.3f)
    }

    fun webpageLoaded() {
        _display.value = Display.Login(authenticating = true, progress = 0.6f)
    }

    fun transitionSettings() {
        _display.value = Display.Settings
    }

    fun transitionLegal() {
        _display.value = Display.Legal
    }

    fun transitionAbout() {
        _display.value = Display.About
    }

    fun transitionPrivacy() {
        _display.value = Display.Privacy
    }

    fun transitionSupport() {
        _display.value = Display.Support
    }

    fun transitionNotifications() {
        _display.value = Display.Notifications
    }

    fun transitionProfile() {
        _display.value = Display.Profile
    }

    fun transitionLogin() {
        _display.value = Display.Login(authenticating = false, progress = 0.0f)
    }

    fun transitionFavorites() {
        _display.value = Display.Favorites
    }

    fun transitionEateryDetail() {
        _display.value = Display.EateryDetailVisible
    }

    fun logout() {
        _state.value = State.Empty
        LoginRepository.saveLoginInfo("", "")
        transitionLogin()
    }

    fun loginSuccess(sessionId: String) {
        // Catch the edge case in which a user logs out before it loads, then the load occurs.
        if (state.value == State.Empty) return

        val isAutoLogin = _state.value is State.AutoLoggingIn
        if (!isAutoLogin) {
            _display.value = Display.Login(authenticating = true, progress = 0.9f)
        }

        viewModelScope.launch {
            val res1 = GetApiService.getInstance().fetchUser(
                GetApiService.generateUserBody(sessionId = sessionId)
            )
            if (res1.exception != null || res1.response == null) {
                loginFailure(LoginFailureType.FETCH_USER_FAILURE)
                return@launch
            }

            if (!isAutoLogin)
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

            val cachedProfile: State.ProfileData? =
                if (_state.value is State.AutoLoggingIn) (_state.value as State.AutoLoggingIn).cachedProfileData else null
            _state.value = State.ProfileData(
                user = user,
                query = cachedProfile?.query ?: "",
                accountFilter = cachedProfile?.accountFilter ?: AccountType.MEALSWIPES,
            )
            if (!isAutoLogin)
                _display.value = Display.Profile

            LoginRepository.cacheAccountInfo(user.accounts!!, user.transactions!!)
        }
    }

    fun loginFailure(error: LoginFailureType) {
        _state.value = State.LoginFailure(error)
        _display.value = Display.Login(authenticating = false)

        shakeLogin()
    }

    fun shakeLogin() {
        CoroutineScope(Dispatchers.Default).launch {
            _display.value = Display.Login(authenticating = false, intOffset = IntOffset(-100, 0))
            delay(40)
            _display.value = Display.Login(authenticating = false, intOffset = IntOffset(100, 0))
            delay(40)
            _display.value = Display.Login(authenticating = false, intOffset = IntOffset(-100, 0))
            delay(40)
            _display.value = Display.Login(authenticating = false, intOffset = IntOffset(100, 0))
            delay(40)
            _display.value = Display.Login(authenticating = false, intOffset = IntOffset(0, 0))
        }
    }

    fun updateAccountFilter(updatedFilter: AccountType) {
        if (_state.value is State.ProfileData) {
            val currentProfileData = _state.value as? State.ProfileData ?: return
            _state.value = State.ProfileData(
                user = currentProfileData.user,
                query = currentProfileData.query,
                accountFilter = updatedFilter
            )
        } else {
            val currentProfileData =
                (_state.value as? State.AutoLoggingIn)?.cachedProfileData ?: return
            currentProfileData.accountFilter = updatedFilter
        }
    }

    fun updateQuery(updatedQuery: String) {
        if (_state.value is State.ProfileData) {
            val currentProfileData = _state.value as? State.ProfileData ?: return
            _state.value = State.ProfileData(
                user = currentProfileData.user,
                query = updatedQuery,
                accountFilter = currentProfileData.accountFilter
            )
        } else {
            val currentProfileData =
                (_state.value as? State.AutoLoggingIn)?.cachedProfileData ?: return
            currentProfileData.query = updatedQuery
        }
    }

}

enum class LoginFailureType {
    USERNAME_PASSWORD_INVALID,
    FETCH_USER_FAILURE,
    FETCH_ACCOUNTS_FAILURE,
    FETCH_TRANSACTION_HISTORY_FAILURE
}
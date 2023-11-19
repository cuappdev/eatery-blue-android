package com.cornellappdev.android.eateryblue.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserRepository
import com.cornellappdev.android.eateryblue.ui.screens.CurrentUser
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var _loginState: MutableStateFlow<LoggingInStatus> =
        MutableStateFlow(LoggingInStatus.NotLoggedIn)
    var loginState = _loginState.asStateFlow()

    var userCredentials = combine(
        userPreferencesRepository.usernameFlow, userPreferencesRepository.passwordFlow
    ) { username, password ->
        Pair(username, password)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Pair("", ""))


//    var isLoggedIn: LoggedInStatus by mutableStateOf(LoggedInStatus.Pending)
//        private set

//    init {
//        getSavedLoginInfo()
//    }

//    fun getSavedLoginInfo() = viewModelScope.launch {
//        loginState = if (userPreferencesRepository.usernameFlow.value.isNotEmpty() && userPreferencesRepository.passwordFlow.value.isNotEmpty()) {
//            val loginInfo = userPreferencesRepository.fetchLoginInfo()
//            LoggedInStatus.IsLoggedIn(loginInfo.first, loginInfo.second)
//        } else {
//            LoggedInStatus.NotLoggedIn
//        }
//    }

    fun saveLoginInfo(username: String, password: String) = viewModelScope.launch {
        // TODO send new username and password to userCredentials flow
        userPreferencesRepository.saveLoginInfo(username, password)
        userPreferencesRepository.setIsLoggedIn(true)
    }

    fun getUser(sessionId: String) = viewModelScope.launch {
        _loginState.value = try {
            val user = userRepository.getUser(sessionId).response!!
            val account = userRepository.getAccount(sessionId, user.id!!).response!!.accounts
            val transactions =
                userRepository.getTransactionHistory(sessionId, user.id).response!!.transactions
            user.accounts = account
            user.transactions = transactions

            CurrentUser.user = user
            LoggingInStatus.LoggingIn(EateryApiResponse.Success(user))
        } catch (e: Exception) {
            Log.d("LOGIN", e.stackTraceToString())
            LoggingInStatus.LoggingIn(EateryApiResponse.Error)
        }
    }

    fun logOut() = viewModelScope.launch {
        CurrentUser.user = null
        userPreferencesRepository.setIsLoggedIn(false)
        userPreferencesRepository.saveLoginInfo("", "")
    }
}


sealed interface LoggingInStatus {
    object NotLoggedIn : LoggingInStatus
    data class LoggingIn(val user: EateryApiResponse<User>) : LoggingInStatus
}
package com.cornellappdev.android.eateryblue.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserRepository
import com.cornellappdev.android.eateryblue.ui.screens.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    var loginState: LoginState by mutableStateOf(LoginState.Pending)
        private set

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

    fun saveLoginInfo(username: String, password: String) = viewModelScope.launch {
        userPreferencesRepository.saveLoginInfo(username, password)
        userPreferencesRepository.setIsLoggedIn(true)
    }

    fun getUser(sessionId: String) = viewModelScope.launch {
        loginState = try {
            val user = userRepository.getUser(sessionId).response!!
            val account = userRepository.getAccount(sessionId, user.id!!).response!!.accounts
            val transactions =
                userRepository.getTransactionHistory(sessionId, user.id).response!!.transactions
            user.accounts = account
            user.transactions = transactions

            CurrentUser.user = user
            LoginState.Success(user)
        } catch (e: Exception) {
            Log.d("LOGIN", e.stackTraceToString())
            LoginState.Error
        }
    }

    fun logOut() = viewModelScope.launch {
        CurrentUser.user = null
        userPreferencesRepository.setIsLoggedIn(false)
        userPreferencesRepository.saveLoginInfo("", "")
    }
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

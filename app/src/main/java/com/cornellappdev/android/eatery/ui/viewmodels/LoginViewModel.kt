package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Account
import com.cornellappdev.android.eatery.data.models.AccountType
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.User
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.screens.CurrentUser
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

    // Convert the state to a flow that can be updated by screens that use the LoginViewModel
    val state = _state.asStateFlow()

    // List of all available meal plans
    val mealPlanList = mutableListOf(
        AccountType.FLEX,
        AccountType.BEAR_TRADITIONAL,
        AccountType.BEAR_CHOICE,
        AccountType.BEAR_BASIC,
        AccountType.UNLIMITED,
        AccountType.HOUSE_AFFILIATE,
        AccountType.HOUSE_MEALPLAN,
        AccountType.JUST_BUCKS,
        AccountType.OFF_CAMPUS
    )

    // Check what the meal plan is against our list of meal plans
    fun checkMealPlan(): Account? {
        if (_state.value !is State.Account || CurrentUser.user == null) return null
        var currAccount: Account? = null
        CurrentUser.user!!.accounts!!.forEach {
            if (mealPlanList.contains(it.type)) {
                currAccount = it
            }
        }
        return currAccount
    }

    fun checkAccount(accountType: AccountType): Account? {
        if (_state.value !is State.Account || CurrentUser.user == null) return null
        return CurrentUser.user!!.accounts!!.find {
            it.type == accountType
        }
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

    fun getTransactionsOfType(accountType: AccountType, query: String): List<Transaction> {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        if (_state.value !is State.Account || CurrentUser.user == null) return listOf()
        return CurrentUser.user!!.transactions?.filter { transaction ->
            transaction.accountType == accountType
                    && LocalDateTime.parse(transaction.date, inputFormatter) >= LocalDateTime.now()
                .minusDays(30)
                    && transaction.location!!.lowercase().contains(query.lowercase())
        } ?: listOf()
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
        val newState = State.Login(
            "", "", null, false
        )
        _state.value = newState
        viewModelScope.launch {
            CurrentUser.user = null
            userPreferencesRepository.setIsLoggedIn(false)
            userPreferencesRepository.saveLoginInfo("", "")
        }
    }

    init {
        getSavedLoginInfo()
    }

    private fun getSavedLoginInfo() = viewModelScope.launch {
        if (userPreferencesRepository.getIsLoggedIn()) {
            val loginInfo = userPreferencesRepository.fetchLoginInfo()
            getUser(loginInfo.first)
        }
    }

    fun getUser(sessionId: String) = viewModelScope.launch {
        try {
            val currState = _state.value
            val user = userRepository.getUser(sessionId).response!!
            val account = userRepository.getAccount(sessionId, user.id!!).response!!.accounts
            val transactions =
                userRepository.getTransactionHistory(sessionId, user.id).response!!.transactions
            user.accounts = account
            user.transactions = transactions
            CurrentUser.user = user

            if (currState is State.Login) {
                userPreferencesRepository.saveLoginInfo(sessionId, currState.password)
                userPreferencesRepository.setIsLoggedIn(true)
            }
            val newState = State.Account(
                user = user,
                query = "",
                accountFilter = AccountType.BRBS
            )
            _state.value = newState
        } catch (e: Exception) {
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
            userPreferencesRepository.saveLoginInfo("", "")
            userPreferencesRepository.setIsLoggedIn(false)
        }
    }
}

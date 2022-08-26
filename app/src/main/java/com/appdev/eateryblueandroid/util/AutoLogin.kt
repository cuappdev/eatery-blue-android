package com.appdev.eateryblueandroid.util

import android.util.Log
import com.appdev.eateryblueandroid.models.*
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.Constants.passwordAlias
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.AccountProto
import com.codelab.android.datastore.Date
import com.codelab.android.datastore.TransactionProto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private var loadedUsername: String? = null
private var loadedPassword: String? = null

/**
 * Attempts to auto login. Only succeeds if loadedUsername AND loadedPassword are non-null
 * and non-empty, AND an auto login has not already taken place.
 */
fun attemptAutoLogin(profileViewModel: ProfileViewModel) {
    if (profileViewModel.state.value !is ProfileViewModel.State.LoggingIn
        && profileViewModel.state.value !is ProfileViewModel.State.ProfileData
        && profileViewModel.state.value !is ProfileViewModel.State.AutoLoggingIn
        && !loadedUsername.isNullOrEmpty() && !loadedPassword.isNullOrEmpty()) {
        CoroutineScope(Dispatchers.Default).launch {
            profileViewModel.autoLogin(
                loadedUsername!!,
                decryptData(passwordAlias, loadedPassword!!)
            )
            Log.i(
                "Login",
                "Attempting Login with username " + loadedUsername + " and password " + loadedPassword
            )
        }
    }
}

/**
 * Asynchronously pulls login net ID and password from proto datastore (local storage),
 * then attempts to log in.
 *
 * @see attemptAutoLogin
 */
fun initializeLoginData(profileViewModel: ProfileViewModel) {
    val usernameFlow: Flow<String> = appContext!!.userPreferencesStore.data
        .map { userPrefs ->
            userPrefs.username
        }
    val passwordFlow: Flow<String> = appContext!!.userPreferencesStore.data
        .map { userPrefs ->
            userPrefs.password
        }
    CoroutineScope(Dispatchers.IO).launch {
        usernameFlow.collect { name ->
            //Use name...
            loadedUsername = name
            attemptAutoLogin(profileViewModel = profileViewModel)

            if (name.isNotEmpty()) {
                this.cancel()
            }
        }
    }
    CoroutineScope(Dispatchers.IO).launch {
        passwordFlow.collect { encryptedPass ->
            //Use password...
            loadedPassword = encryptedPass
            attemptAutoLogin(profileViewModel = profileViewModel)

            if (encryptedPass.isNotEmpty()) {
                this.cancel()
            }
        }
    }
}

/** Saves a username and password to local storage. Only call upon a successful login.*/
fun saveLoginInfo(username: String, password: String) {
    loadedUsername = username
    loadedPassword = password
    val loggedIn = username.isNotEmpty() && password.isNotEmpty()
    if (!loggedIn)
        CachedAccountInfo.cached = false

    Log.i("IO", "Attempting save with username $username and its password.")

    // Save to proto Datastore
    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setUsername(username)
                .setPassword(encryptData(passwordAlias, password))
                .setWasLoggedIn(loggedIn)
                .build()
        }
    }
}

fun ordinalToAccountType(ordinal: Int): AccountType {
    return AccountType.values()[ordinal]
}

fun ordinalToTransactionType(ordinal: Int): TransactionType {
    return TransactionType.values()[ordinal]
}


object CachedAccountInfo {
    var accounts: MutableList<Account> = mutableListOf()
    var transactions: MutableList<Transaction> = mutableListOf()
    var cached: Boolean = false
}

/**
 * Checks if the profile should show cached data, then initializes the cached data if so.
 * Loads from local storage, so both of these steps are done asynchronously.
 */
fun checkProfileCache() {
    val loggedInFlow: Flow<Boolean> = appContext!!.userPreferencesStore.data
        .map { userPrefs ->
            userPrefs.wasLoggedIn
        }

    CoroutineScope(Dispatchers.IO).launch {
        loggedInFlow.collect { loggedIn ->
            if (loggedIn) {
                CachedAccountInfo.cached = true
                initializeCachedAccountInfo()
            }
            this.cancel()
        }
    }
}

private fun initializeCachedAccountInfo() {
    Log.i("Caching", "Caching request received. Caching... ")
    val cache = CachedAccountInfo
    val accountFlow: Flow<List<AccountProto>> = appContext!!.userPreferencesStore.data
        .map { userPrefs ->
            userPrefs.accountsList
        }
    val transactionFlow: Flow<List<TransactionProto>> = appContext!!.userPreferencesStore.data
        .map { userPrefs ->
            userPrefs.transactionHistoryList
        }

    CoroutineScope(Dispatchers.IO).launch {
        accountFlow.collect { accounts ->
            accounts.forEach { accProto ->
                cache.accounts.add(Account(ordinalToAccountType(accProto.type), accProto.balance))
            }
            Log.i("Caching", "Accounts Caching done with " + cache.accounts.size + " items pulled.")
            if (cache.accounts.size > 0) this.cancel()
        }
    }

    CoroutineScope(Dispatchers.IO).launch {
        transactionFlow.collect { transactions ->
            transactions.forEach { transProto ->
                val date = transProto.date
                cache.transactions.add(
                    Transaction(
                        transProto.id,
                        transProto.amount,
                        transProto.resultingBalance,
                        LocalDateTime.of(date.year, date.month, date.day, date.hour, date.minute),
                        ordinalToTransactionType(transProto.transactionType),
                        ordinalToAccountType(transProto.accountType),
                        transProto.location
                    )
                )
            }
            Log.i(
                "Caching",
                "Transactions History Caching done with " + cache.transactions.size + " items pulled."
            )
            if (cache.transactions.size > 0) this.cancel()
        }
    }
}

fun cacheAccountInfo(accounts: List<Account>, transactions: List<Transaction>) {
    val accMutable: MutableList<AccountProto> = mutableListOf()
    accounts.forEach { acc ->
        accMutable.add(
            AccountProto.newBuilder().setType(acc.type!!.ordinal).setBalance(acc.balance ?: 0.0)
                .build()
        )
    }
    val transactionsMutable: MutableList<TransactionProto> = mutableListOf()
    transactions.forEach { trans ->
        // Some transactions (like laundry depositing for some reason??) have no date.
        // Not important though since those transactions are not displayed anyways,
        // so just put some default date:
        val date: LocalDateTime = trans.date ?: LocalDateTime.of(1, 1, 1, 1, 1)

        transactionsMutable.add(
            TransactionProto.newBuilder()
                .setId(trans.id)
                .setAmount(trans.amount!!)
                .setResultingBalance(trans.resultingBalance ?: 0.0)
                .setDate(
                    Date.newBuilder()
                        .setYear(date.year)
                        .setMonth(date.month.value)
                        .setDay(date.dayOfMonth)
                        .setHour(date.hour)
                        .setMinute(date.minute)
                        .build()
                )
                .setTransactionType(trans.transactionType!!.ordinal)
                .setAccountType(trans.accountType!!.ordinal)
                .setLocation(trans.location!!)
                .build()
        )
    }

    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearAccounts()
                .addAllAccounts(accMutable)
                .clearTransactionHistory()
                .addAllTransactionHistory(transactionsMutable)
                .build()
        }
    }
}

fun makeCachedUser(): User {
    if (!CachedAccountInfo.cached) return User()

    return User(
        id = loadedUsername,
        accounts = CachedAccountInfo.accounts,
        transactions = CachedAccountInfo.transactions
    )
}

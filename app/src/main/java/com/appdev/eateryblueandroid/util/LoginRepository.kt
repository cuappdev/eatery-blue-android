package com.appdev.eateryblueandroid.util

import android.util.Log
import com.appdev.eateryblueandroid.models.*
import com.appdev.eateryblueandroid.ui.appContext
import com.appdev.eateryblueandroid.util.Constants.passwordAlias
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.AccountProto
import com.codelab.android.datastore.Date
import com.codelab.android.datastore.TransactionProto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

object LoginRepository {
    data class LoginState(
        var username: String = "",
        var encryptedPassword: String = "",
        var accounts: List<Account>? = null,
        var transactions: List<Transaction>? = null
    ) {
        fun ready() =
            username.isNotEmpty() && encryptedPassword.isNotEmpty() && !accounts.isNullOrEmpty() && !transactions.isNullOrEmpty()
    }

    private var loginStateFlow: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    var loginFlow = loginStateFlow.asStateFlow()

    /**
     * Makes a new LoginState object by supplying new fields for a LoginState.
     * If any of the parameters [newUsername], [newPassword], [newAccounts], or [newTransactions]
     * are not passed, the previous values will be used instead.
     */
    private fun makeNewLoginState(
        newUsername: String? = null,
        newPassword: String? = null,
        newAccounts: List<Account>? = null,
        newTransactions: List<Transaction>? = null
    ): LoginState {
        val state = loginStateFlow.value
        return LoginState(
            username = newUsername ?: state.username,
            encryptedPassword = newPassword ?: state.encryptedPassword,
            accounts = newAccounts ?: state.accounts,
            transactions = newTransactions ?: state.transactions
        )
    }

    /**
     * Asynchronously pulls login net ID and password from proto datastore (local storage).
     * Also pulls cached login data (transactions and accounts) if such information has
     * not yet been pulled.
     */
    fun initializeLoginData() {
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
                loginStateFlow.value = makeNewLoginState(newUsername = name)

                if (name.isNotEmpty()) {
                    this.cancel()
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            passwordFlow.collect { encryptedPass ->
                //Use password...
                loginStateFlow.value = makeNewLoginState(newPassword = encryptedPass)

                if (encryptedPass.isNotEmpty()) {
                    this.cancel()
                }
            }
        }

        checkProfileCache()
    }

    /** Saves a username and password to local storage. Only call upon a successful login, or with
     * empty strings for username and password upon a logout.*/
    fun saveLoginInfo(username: String, password: String) {
        loginStateFlow.value = makeNewLoginState(newUsername = username, newPassword = password)

        Log.i("IO", "Attempting save with username $username and its password.")

        // Save to proto Datastore
        CoroutineScope(Dispatchers.IO).launch {
            appContext!!.userPreferencesStore.updateData { currentPreferences ->
                currentPreferences.toBuilder()
                    .setUsername(username)
                    .setPassword(encryptData(passwordAlias, password))
                    .setWasLoggedIn(username.isNotEmpty() && password.isNotEmpty())
                    .build()
            }
        }
    }

    private fun ordinalToAccountType(ordinal: Int): AccountType {
        return AccountType.values()[ordinal]
    }

    private fun ordinalToTransactionType(ordinal: Int): TransactionType {
        return TransactionType.values()[ordinal]
    }

    /**
     * Checks if the profile should show cached data, then initializes the cached data if so.
     * Loads from local storage, so both of these steps are done asynchronously.
     */
    private fun checkProfileCache() {
        val loggedInFlow: Flow<Boolean> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                userPrefs.wasLoggedIn
            }

        CoroutineScope(Dispatchers.IO).launch {
            loggedInFlow.collect { wasLoggedIn ->
                if (wasLoggedIn) {
                    initializeCachedAccountInfo()
                }
                this.cancel()
            }
        }
    }

    private fun initializeCachedAccountInfo() {
        Log.i("Caching", "Caching request received. Caching... ")
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
                val mutableAccounts: MutableList<Account> = mutableListOf()
                accounts.forEach { accProto ->
                    mutableAccounts.add(
                        Account(
                            ordinalToAccountType(accProto.type),
                            accProto.balance
                        )
                    )
                }

                Log.i(
                    "Caching",
                    "Accounts Caching done with " + mutableAccounts.size + " items pulled."
                )

                loginStateFlow.value = makeNewLoginState(newAccounts = mutableAccounts)

                if (mutableAccounts.isNotEmpty()) {
                    this.cancel()
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            transactionFlow.collect { transactions ->
                val mutableTransactions: MutableList<Transaction> = mutableListOf()
                transactions.forEach { transProto ->
                    val date = transProto.date
                    mutableTransactions.add(
                        Transaction(
                            transProto.id,
                            transProto.amount,
                            transProto.resultingBalance,
                            LocalDateTime.of(
                                date.year,
                                date.month,
                                date.day,
                                date.hour,
                                date.minute
                            ),
                            ordinalToTransactionType(transProto.transactionType),
                            ordinalToAccountType(transProto.accountType),
                            transProto.location
                        )
                    )
                }

                Log.i(
                    "Caching",
                    "Transactions History Caching done with " + mutableTransactions.size + " items pulled."
                )

                loginStateFlow.value = makeNewLoginState(newTransactions = mutableTransactions)

                if (mutableTransactions.isNotEmpty()) {
                    this.cancel()
                }
            }
        }
    }

    /**
     * Saves user profile data to proto datastore.
     */
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

    /**
     * Makes a "dummy" cached user based off cached/saved profile data.
     * If called before profile data has been properly initialized from local storage, returns an empty user.
     * Thus, make sure you have loaded the profile cache before calling.
     *
     * @see checkProfileCache
     */
    fun makeCachedUser(): User {
        val state = loginStateFlow.value
        if (!state.ready()) return User()

        return User(
            id = state.username,
            accounts = state.accounts,
            transactions = state.transactions
        )
    }
}
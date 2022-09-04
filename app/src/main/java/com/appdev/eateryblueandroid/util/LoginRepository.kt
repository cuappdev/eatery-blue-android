package com.appdev.eateryblueandroid.util

import android.util.Log
import com.appdev.eateryblueandroid.models.*
import com.appdev.eateryblueandroid.ui.appContext
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.Constants.passwordAlias
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.AccountProto
import com.codelab.android.datastore.Date
import com.codelab.android.datastore.TransactionProto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

object LoginRepository {
    private var loadedUsername: String? = null
    private var loadedPassword: String? = null

    private var accountsFlow: MutableStateFlow<MutableList<Account>> = MutableStateFlow(mutableListOf())
    var accounts = accountsFlow.asStateFlow()


    private var transactionsFlow : MutableStateFlow<MutableList<Transaction>> = MutableStateFlow(mutableListOf())
    var transactions = transactionsFlow.asStateFlow()

    /** True if the caching process has completed fully. False otherwise. */
    private var cachedFlow : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var cached = cachedFlow.asStateFlow()

    /**
     * Attempts to auto login. Only succeeds if loadedUsername AND loadedPassword are non-null
     * and non-empty, AND any kind of login has not already taken place.
     */
    fun attemptAutoLogin(profileViewModel: ProfileViewModel) {
        if (profileViewModel.state.value !is ProfileViewModel.State.LoggingIn
            && profileViewModel.state.value !is ProfileViewModel.State.ProfileData
            && profileViewModel.state.value !is ProfileViewModel.State.AutoLoggingIn
            && cached.value && !loadedUsername.isNullOrEmpty() && !loadedPassword.isNullOrEmpty()
        ) {
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
            cachedFlow.value = false

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

    /**
     * Checks if the profile should show cached data, then initializes the cached data if so.
     * Loads from local storage, so both of these steps are done asynchronously.
     */
    fun checkProfileCache(profileViewModel: ProfileViewModel) {
        val loggedInFlow: Flow<Boolean> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                userPrefs.wasLoggedIn
            }

        CoroutineScope(Dispatchers.IO).launch {
            loggedInFlow.collect { wasLoggedIn ->
                if (wasLoggedIn) {
                    initializeCachedAccountInfo(profileViewModel)
                }
                this.cancel()
            }
        }
    }

    private fun initializeCachedAccountInfo(profileViewModel: ProfileViewModel) {
        Log.i("Caching", "Caching request received. Caching... ")
        val accountFlow: Flow<List<AccountProto>> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                userPrefs.accountsList
            }
        val transactionFlow: Flow<List<TransactionProto>> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                userPrefs.transactionHistoryList
            }

        var hasLoadedAccounts = false
        var hasLoadedTransactions = false

        CoroutineScope(Dispatchers.IO).launch {
            accountFlow.collect { accounts ->
                val mutableAccounts : MutableList<Account> = mutableListOf()
                accounts.forEach { accProto ->
                    mutableAccounts.add(
                        Account(
                            ordinalToAccountType(accProto.type),
                            accProto.balance
                        )
                    )
                }
                accountsFlow.value = mutableAccounts

                Log.i(
                    "Caching",
                    "Accounts Caching done with " + accountsFlow.value.size + " items pulled."
                )

                hasLoadedAccounts = true
                cachedFlow.value = hasLoadedTransactions
                attemptAutoLogin(profileViewModel)

                if (accountsFlow.value.size > 0) {
                    this.cancel()
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            transactionFlow.collect { transactions ->
                val mutableTransactions : MutableList<Transaction> = mutableListOf()
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
                transactionsFlow.value = mutableTransactions

                Log.i(
                    "Caching",
                    "Transactions History Caching done with " + transactionsFlow.value.size + " items pulled."
                )

                hasLoadedTransactions = true
                cachedFlow.value = hasLoadedAccounts
                attemptAutoLogin(profileViewModel)

                if (transactionsFlow.value.isNotEmpty()) {
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
        if (!cachedFlow.value) return User()

        return User(
            id = loadedUsername,
            accounts = accounts.value,
            transactions = transactions.value
        )
    }
}
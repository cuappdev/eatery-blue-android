package com.appdev.eateryblueandroid.util

import android.util.Log
import com.appdev.eateryblueandroid.models.Account
import com.appdev.eateryblueandroid.models.Transaction
import com.appdev.eateryblueandroid.util.Constants.passwordAlias
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

var loadedUsername : String? = null
var loadedPassword : String? = null

fun loadData() : Job {
    val job: Job = CoroutineScope(Dispatchers.IO).launch {
        if (loadedUsername != null && loadedPassword != null) this.cancel()

        var username = ""
        var password = ""

        fun tryLoad() {
            Log.i("IO", "Attempting auto load with username $username and password $password.")
            if (username.isNotEmpty() && password.isNotEmpty()) {
                loadedUsername = username
                loadedPassword = decryptData(passwordAlias, password)
                Log.i("IO", "Username and password loaded: $username, $password")
                this.cancel()
            }
        }

        val usernameFlow: Flow<String> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                // The username property is generated from the proto schema.
                userPrefs.username
            }
        val passwordFlow: Flow<String> = appContext!!.userPreferencesStore.data
            .map {userPrefs ->
                userPrefs.password
            }
        CoroutineScope(Dispatchers.IO).launch {
            usernameFlow.collect { name ->
                //Use name...
                username = name
                tryLoad()
                if (name.isNotEmpty())
                    this.cancel()
            }
        }
        passwordFlow.collect { encryptedPass ->
            //Use password...
            password = encryptedPass
            //De-encrypt...
            tryLoad()
        }
    }
    return job
}

/** Saves a username and password to local storage. */
fun saveLoginInfo(username: String, password: String) {

    loadedUsername = username
    loadedPassword = password

    Log.i("IO", "Attempting save with username $username and its password.")

    // Save to proto Datastore
    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setUsername(username)
                .setPassword(encryptData(passwordAlias, password))
                .build()
        }
    }
}

fun cacheAccountInfo(accounts : List<Account>, transactions : List<Transaction>) {
    /*CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setUsername(username)
                .setPassword(password)
                .build()
        }
    }*/
}
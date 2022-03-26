package com.appdev.eateryblueandroid.util

import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.AccountProto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


/**
 * A list of ints corresponding to the recent ID's that the user has
 * searched and clicked on.
 *
 * Use saveRecentSearch(search) to add recent searches to the list.
 *
 * Index 0 corresponds to the newest search, index size-1 is the oldest search.
 */
val recentSearches : List<Int> = mutableListOf()

fun initializeRecentSearches() {
    val recentSearchesFlow: Flow<List<Int>> = appContext!!.userPreferencesStore.data
        .map { userPrefs ->
            userPrefs.recentSearchesList
        }

    CoroutineScope(Dispatchers.IO).launch {
        recentSearchesFlow.collect { pulledSearches ->
            pulledSearches.forEach {
                (recentSearches as MutableList<Int>).add(it)
            }
            this.cancel()
        }
    }
}


/**
 * Adds a recent search with the given index and saves to local storage.
 */
fun saveRecentSearch(search : Int) {
    val index = recentSearches.indexOf(search)
    if (index >= 0)
        (recentSearches as MutableList<Int>).removeAt(index)
    (recentSearches as MutableList<Int>).add(0, search)

    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearRecentSearches()
                .addAllRecentSearches(recentSearches)
                .build()
        }
    }
}

/**
 * Removes a recent search with the given index and saves to local storage.
 */
fun removeRecentSearchAt(index : Int) {
    if (index >= 0 && index < recentSearches.size) {
        (recentSearches as MutableList<Int>).removeAt(index)
    }

    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearRecentSearches()
                .addAllRecentSearches(recentSearches)
                .build()
        }
    }
}
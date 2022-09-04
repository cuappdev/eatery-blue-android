package com.appdev.eateryblueandroid.util

import com.appdev.eateryblueandroid.ui.appContext
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.AccountProto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object RecentSearchesRepository {
    /**
     * A list of ints corresponding to the recent ID's that the user has
     * searched and clicked on.
     *
     * Use saveRecentSearch(search) to add recent searches to the list.
     *
     * Index 0 corresponds to the newest search, index size-1 is the oldest search.
     */
    private val recentSearchesFlow: MutableStateFlow<MutableList<Int>> = MutableStateFlow(mutableListOf())
    val recentSearches = recentSearchesFlow.asStateFlow()

    fun initializeRecentSearches() {
        val recentSearchesStorageFlow: Flow<List<Int>> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                userPrefs.recentSearchesList
            }

        CoroutineScope(Dispatchers.IO).launch {
            recentSearchesStorageFlow.collect { pulledSearches ->
                val searches : MutableList<Int> = mutableListOf()
                pulledSearches.forEach {
                    searches.add(it)
                }
                recentSearchesFlow.value = searches
                this.cancel()
            }
        }
    }


        /**
     * Adds a recent search with the given index and saves to local storage.
     */


    /**
     * Adds a recent search with the given index and saves to local storage.
     */
    fun saveRecentSearch(search: Int) {
        val searches = recentSearches.value
        val index = searches.indexOf(search)

        if (index >= 0)
            searches.removeAt(index)
        searches.add(0, search)

        recentSearchesFlow.value = searches

        CoroutineScope(Dispatchers.IO).launch {
            appContext!!.userPreferencesStore.updateData { currentPreferences ->
                currentPreferences.toBuilder()
                    .clearRecentSearches()
                    .addAllRecentSearches(searches)
                    .build()
            }
        }
    }

        /**
     * Removes a recent search with the given index and saves to local storage.
     */

    /**
     * Removes a recent search with the given index and saves to local storage.
     */
    fun removeRecentSearchAt(index: Int) {
        if (index >= 0 && index < recentSearches.value.size) {
            val searches = recentSearches.value
            searches.removeAt(index)
            recentSearchesFlow.value = searches
        }

        CoroutineScope(Dispatchers.IO).launch {
            appContext!!.userPreferencesStore.updateData { currentPreferences ->
                currentPreferences.toBuilder()
                    .clearRecentSearches()
                    .addAllRecentSearches(recentSearches.value)
                    .build()
            }
        }
    }
}

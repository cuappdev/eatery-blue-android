package com.appdev.eateryblueandroid.util

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.appdev.eateryblueandroid.models.Eatery
import com.codelab.android.datastore.UserPreferences
import com.codelab.android.datastore.UserPreferencesSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

private var myFavorites: Favorites = Favorites(mutableMapOf())
var appContext: Context? = null
private var favoriteStates: HashMap<Int, MutableState<Boolean>> = hashMapOf()

private data class Favorites(var favoritesMap: MutableMap<Int?, Boolean>)

fun toggleFavorite(eatery: Eatery) {
    eatery.isFavorite = !eatery.isFavorite
    favoriteStates[eatery.id]!!.value = !favoriteStates[eatery.id]!!.value
    myFavorites.favoritesMap[eatery.id] = eatery.isFavorite

    //Save
    saveFavoriteMap()
}

/**
 * Gets a MutableState observing the favorite status of the eatery.
 * Use when a live state of the eatery's favorite status is needed.
 *
 * @param eatery    The eatery whose favorite state to get.
 */
fun getMutableFavoriteStateOf(eatery: Eatery): MutableState<Boolean> {
    if (favoriteStates.containsKey(eatery.id)) {
        return favoriteStates[eatery.id]!!
    }
    val state = mutableStateOf(myFavorites.favoritesMap[eatery.id] == true)
    favoriteStates[eatery.id!!] = state
    return state
}

fun numFavorites(): Int {
    if (myFavorites.favoritesMap.isEmpty()) return 0
    var num = 0
    myFavorites.favoritesMap.keys.forEach { mapping ->
        if (myFavorites.favoritesMap[mapping] == true) num++
    }
    return num
}

fun isFavorite(eatery: Eatery): Boolean {
    return getMutableFavoriteStateOf(eatery).value
}

private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer
)

fun initializeFavoriteMap() {
    CoroutineScope(Dispatchers.IO).launch {
        val favoritesFlow: Flow<Map<Int, Boolean>> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                // The favoritesMap property is generated from the proto schema.
                userPrefs.favoritesMap
            }

        favoritesFlow.collect { map -> myFavorites.favoritesMap = map.toMutableMap() }
    }
    myFavorites.favoritesMap.keys.forEach { mapping ->
        favoriteStates[mapping!!] = mutableStateOf(myFavorites.favoritesMap[mapping]!!)
    }
}

fun saveFavoriteMap() {
    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .putAllFavorites(myFavorites.favoritesMap)
                .build()
        }
    }
}
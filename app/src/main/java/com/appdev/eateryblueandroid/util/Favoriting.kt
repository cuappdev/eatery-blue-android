package com.appdev.eateryblueandroid.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.util.Constants.DATA_STORE_FILE_NAME
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

var appContext: Context? = null
private val favoriteMap: HashMap<Int, Boolean> = hashMapOf()

/*private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer
)*/

private var fetchedStates: Boolean = false

/**
 * Initializes favoriteStates with mutableStates for each eatery (id).
 * Also initializes any eateries' isFavorite states that are passed.
 * The states of the eatery and favoriteStates will point to the same reference.
 *
 * @param eateries  List of eateries whose states to initialize.
 */
fun initializeFavoriteMap(eateries: List<Eatery> = listOf()) {
    /*
    * For some reason, this function attempts to call COLLECT (below) multiple times, even after
    * the app has launched. To counteract this, introduce a boolean (fetchedStates) to make it run only once.
    * After all, we only want to load the favorites ONCE: when we first open the app.
    */
    CoroutineScope(Dispatchers.IO).launch {
        val favoritesFlow: Flow<Map<Int, Boolean>> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                // The favoritesMap property is generated from the proto schema.
                userPrefs.favoritesMap
            }
        favoritesFlow.collect { map ->
            if (!fetchedStates) {
                fetchedStates = true
                map.keys.forEach { mapping ->
                    favoriteMap[mapping] = map[mapping]!!
                }
            }
            eateries.forEach { eatery ->
                if (favoriteMap.containsKey(eatery.id) && (favoriteMap[eatery.id]!! != eatery.isFavorite())) {
                    eatery.toggleFavorite()
                }
            }
        }
    }
}

/** Saves an eatery's favorite status to local storage. */
fun saveFavorite(eateryId: Int, favorited: Boolean) {
    favoriteMap[eateryId] = favorited

    // Save to proto Datastore
    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .putAllFavorites(favoriteMap)
                .build()
        }
    }
}
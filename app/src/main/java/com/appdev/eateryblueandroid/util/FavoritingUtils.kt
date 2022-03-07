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

var appContext: Context? = null

/** A map of eateries' ids to the mutable states containing their favorite status.
 *  If an eatery's ID is NOT in this map, that MUST mean it is not a favorite.
 *  Otherwise, its favorite status corresponds to the mutable state's value.
 *
 *  Reading directly from this map can be unsafe--use getMutableFavoriteStateOf instead.
 */
private var favoriteStates: HashMap<Int, MutableState<Boolean>> = hashMapOf()

/**
 * Gets a MutableState observing the favorite status of the eatery whose id is known.
 * Use only when you only have the eatery id. If eatery object can be referenced, use
 * eatery.isFavorite instead.
 *
 * @param id    The id corresponding to the eatery whose favorite state to get.
 */
fun getMutableFavoriteStateOf(id: Int): MutableState<Boolean> {
    // If the mutable state does not exist yet in this map, enter a "False" into the map.
    if (!favoriteStates.containsKey(id)) {
        favoriteStates[id] = mutableStateOf(false)
    }
    return favoriteStates[id]!!
}

fun numFavorites(): Int {
    if (favoriteStates.isEmpty()) return 0
    var num = 0
    favoriteStates.keys.forEach { mapping ->
        if (getMutableFavoriteStateOf(mapping).value) num++
    }
    return num
}

private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer
)

private var fetchedStates: Boolean = false

/**
 * Initializes favoriteStates with mutableStates for each eatery (id).
 * Also initializes any eateries' isFavorite states that are passed.
 * The states of the eatery and favoriteStates will point to the same reference.
 *
 * @param eateries  Eateries whose states to initialize as well.
 */
fun initializeFavoriteMap(eateries : List<Eatery> = listOf()) {
    /*
    * For some reason, this function attempts to call COLLECT (below) multiple times, even after
    * the app has launched. To counteract this, introduce a boolean (fetchedStates) to make it run only once.
    * After all, we only want to load the favorites ONCE when we first open the app.
    * Otherwise, many nasty bugs arise.
    */
    CoroutineScope(Dispatchers.IO).launch {
        val favoritesFlow: Flow<Map<Int, Boolean>> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                // The favoritesMap property is generated from the proto schema.
                userPrefs.favoritesMap
            }
        favoritesFlow.collect { map ->
            val tempMap = map.toMutableMap()
            if (!fetchedStates) {
                fetchedStates = true
                tempMap.keys.forEach { mapping ->
                    favoriteStates[mapping] = mutableStateOf(tempMap[mapping] == true)
                }
            }
            eateries.forEach { eatery ->
                if (favoriteStates.containsKey(eatery.id)) {
                    eatery.setFavoriteState(favoriteStates[eatery.id]!!)
                }
                else {
                    val state = mutableStateOf(false)
                    favoriteStates[eatery.id!!] = state
                    eatery.setFavoriteState(state)
                }
            }

        }
    }
}

fun saveFavoriteMap() {
    // Builds a map to save to proto Datastore
    val tempMap: MutableMap<Int, Boolean> = mutableMapOf()
    favoriteStates.keys.forEach { mapping ->
        tempMap[mapping] = favoriteStates[mapping]!!.value
    }

    // Save to proto Datastore
    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .putAllFavorites(tempMap)
                .build()
        }
    }
}
package com.appdev.eateryblueandroid.util

import android.content.Context
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

//private const val USER_PREFERENCES_NAME = "user_preferences"
private const val DATA_STORE_FILE_NAME = "user_prefs.pb"
//private const val SORT_ORDER_KEY = "sort_order"

private var myFavorites: Favorites = Favorites(mutableMapOf())
var appContext: Context? = null

private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer
)

private data class Favorites(
    var favoritesMap: MutableMap<Int?, Boolean>
)

fun toggleFavorite(eatery: Eatery) {
    eatery.isFavorite = !eatery.isFavorite
    myFavorites.favoritesMap[eatery.id] = eatery.isFavorite

    //Save
    saveFavoriteMap()
}

fun numFavorites(): Int {
    if (myFavorites.favoritesMap.isEmpty()) return 0
    var num = 0
    myFavorites.favoritesMap.keys.forEach { mapping ->
        if (myFavorites.favoritesMap[mapping] == true) num++
    }
    return num
}

fun initializeFavoriteMap() {
    CoroutineScope(Dispatchers.IO).launch {
        val favoritesFlow: Flow<Map<Int, Boolean>> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                // The eateryId property is generated from the proto schema.
                userPrefs.favoritesMap
            }

        favoritesFlow.collect { map -> myFavorites.favoritesMap = map.toMutableMap() }
    }
}

fun isFavorite(eatery: Eatery): Boolean {
    return myFavorites.favoritesMap[eatery.id] ?: return false
}

/*
*
* Old version for SharedPreferences
*
fun saveFavoriteSet(): Boolean {
    val mSharedPreferences: SharedPreferences? =
        appContext?.getSharedPreferences(Constants.FAVORITE_EATERIES_LOCAL_STORAGE, 0)
    val gson = Gson()
    val json: String = gson.toJson(myFavorites).toString()
    val mPreferencesEditor = mSharedPreferences?.edit()
    mPreferencesEditor?.putString(Constants.FAVORITE_EATERIES_KEY, json)
    if (mPreferencesEditor != null) return mPreferencesEditor.commit()
    return false
}
*/

fun saveFavoriteMap() {
    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .putAllFavorites(myFavorites.favoritesMap)
                .build()
        }
    }
}
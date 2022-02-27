package com.appdev.eateryblueandroid.util

import android.content.Context
import android.content.SharedPreferences
import com.appdev.eateryblueandroid.models.Eatery
import com.google.gson.Gson

private var myFavorites : Favorites? = null
var appContext : Context? = null

data class Favorites (
    var favoritedSet: HashSet<Int?>? = null
)

/**
 * Returns the number of eateries this user has favorited.
 */
fun numFavorites() : Int {
    return getFavoritedSet().size
}

/**
 * Returns a list of IDs corresponding to the eateries this user has favorited.
 */
fun getFavoritedEateryIDs() : Set<Int?> {
    return getFavoritedSet()
}

/**
 * Toggles favorited status on an eatery.
 *
 * @param eatery    The eatery whose favorited status to toggle.
 */
fun toggleFavorite(
    eatery: Eatery,
) {
    eatery.isFavorite = !eatery.isFavorite
    if (myFavorites == null) initializeFavoriteSet()
    when (eatery.isFavorite) {
        true -> {
            myFavorites?.favoritedSet?.add(eatery.id)}
        false -> {
            myFavorites?.favoritedSet?.remove(eatery.id)
        }
    }
    //Save
    saveFavoritedSet()
}

private fun getFavoritedSet() : HashSet<Int?> {
    if (myFavorites == null) {
        initializeFavoriteSet()
    }
    return myFavorites!!.favoritedSet!!
}

private fun initializeFavoriteSet() {
    val mSharedPreferences: SharedPreferences? = appContext?.getSharedPreferences(Constants.FAVORITE_EATERIES_LOCAL_STORAGE, 0)
    when (val json: String? = mSharedPreferences?.getString(Constants.FAVORITE_EATERIES_KEY, null)) {
        null -> {
            val favoritedSet: HashSet<Int?> = hashSetOf()
            myFavorites = Favorites(favoritedSet)
        }
        "null" -> {
            val favoritedSet: HashSet<Int?> = hashSetOf()
            myFavorites = Favorites(favoritedSet)
        }
        else -> {
            val gson = Gson()
            myFavorites = gson.fromJson(json, Favorites::class.java)
        }
    }
}

/**
 * Returns the favorited status of an eatery
 *
 * @param eatery    The eatery whose favorited status to return.
 */
fun isFavorite(eatery : Eatery) : Boolean {
    return getFavoritedSet().contains(eatery.id)
}

/**
 * Saves the current favorites HashSet to SharedPreferences.
 */
fun saveFavoritedSet() : Boolean {
    val mSharedPreferences : SharedPreferences? = appContext?.getSharedPreferences(Constants.FAVORITE_EATERIES_LOCAL_STORAGE, 0)
    val gson = Gson()
    val json :String= gson.toJson(myFavorites).toString()
    val mPreferencesEditor = mSharedPreferences?.edit()
    mPreferencesEditor?.putString(Constants.FAVORITE_EATERIES_KEY, json)
    if (mPreferencesEditor != null) return mPreferencesEditor.commit()
    return false
}
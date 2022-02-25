package com.appdev.eateryblueandroid.util

import android.content.Context
import android.content.SharedPreferences
import com.appdev.eateryblueandroid.models.Eatery
import com.google.gson.Gson

var myFavorites : Favorites? = null
var appContext : Context? = null

data class Favorites (
    var favoritedSet: HashSet<Int?>? = null
)

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

private fun getFavoritedSet() : HashSet<Int?>? {
    if (myFavorites == null) {
        initializeFavoriteSet()
    }
    return myFavorites?.favoritedSet
}

private fun initializeFavoriteSet() {
    val mSharedPreferences: SharedPreferences? = appContext?.getSharedPreferences("FAV", 0)
    when (val json: String? = mSharedPreferences?.getString("JSON_DATA", null)) {
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

fun isFavorite(eatery : Eatery) : Boolean {
    return getFavoritedSet()?.contains(eatery.id) ?: false
}

fun saveFavoritedSet() : Boolean {
    val mSharedPreferences : SharedPreferences? = appContext?.getSharedPreferences("FAV", 0)
    val gson = Gson()
    val json :String= gson.toJson(myFavorites).toString()
    val mPreferencesEditor = mSharedPreferences?.edit()
    mPreferencesEditor?.putString("JSON_DATA", json)
    if (mPreferencesEditor != null) return mPreferencesEditor.commit()
    return false
}
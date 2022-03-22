package com.appdev.eateryblueandroid.util

import android.content.Context
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.codelab.android.datastore.UserPreferences

object Constants {
    const val BACKEND_URL = "https://eatery-dev.cornellappdev.com/"
    const val GET_BACKEND_URL = "https://services.get.cbord.com/GETServices/services/json/"
    const val SESSIONID_WEBVIEW_URL = "https://get.cbord.com/cornell/full/login.php?mobileapp=1"
    const val SESSIONID_CU_LOGIN_SUBSTRING_IDENTIFIER = "shibidp"
    const val AVERAGE_WALK_SPEED = 1.42
    const val WORLD_DISTANCE_KM = 250000

    const val CORNELL_INSTITUTION_ID = "73116ae4-22ad-4c71-8ffd-11ba015407b1"

    const val DATA_STORE_FILE_NAME = "user_prefs.pb"

    var currentLocation: Location? = null

    val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = UserPreferencesSerializer
    )

    val passwordAlias = "passwordy password"
}
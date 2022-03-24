package com.appdev.eateryblueandroid.util

import android.content.Context
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.appdev.eateryblueandroid.models.SwipesType
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

    val mealPlans = listOf("meal plan", "off-campus value", "bear traditional", "unlimited",
        "bear choice", "bear basic", "house", "flex")

    val mealPlanMap = hashMapOf(
        "off-campus value" to SwipesType.SEMESTERLY,
        "bear traditional" to SwipesType.WEEKLY,
        "unlimited" to SwipesType.UNLIMITED,
        "bear basic" to SwipesType.WEEKLY,
        "bear choice" to SwipesType.WEEKLY,
        "house meal plan" to SwipesType.UNLIMITED,
        "house affiliate" to SwipesType.WEEKLY,
        "flex" to SwipesType.SEMESTERLY)

    // Each of these will have "Meal Plan" appended to it when displayed to the user.
    val mealPlanNameMap = hashMapOf(
        "off-campus value" to "Off-Campus Value",
        "bear traditional" to "Bear Traditional",
        "unlimited" to "Unlimited",
        "bear basic" to "Bear Basic",
        "bear choice" to "Bear Choice",
        "house meal plan" to "House",
        "house affiliate" to "House Affiliate",
        "flex" to "Flex 10/500",
        "bucks" to "Just Bucks")
}
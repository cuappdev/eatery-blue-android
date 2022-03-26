package com.appdev.eateryblueandroid.util

import android.content.Context
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.appdev.eateryblueandroid.models.AccountType.*
import com.appdev.eateryblueandroid.models.AccountType
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

    const val passwordAlias = "passwordy password"

    val mealPlans = listOf(
        "off-campus value", "bear traditional", "unlimited",
        "bear choice", "bear basic", "house", "flex"
    )

    val mealPlanAccountMap: HashMap<String, AccountType> = hashMapOf(
        "off-campus value" to OFF_CAMPUS,
        "bear traditional" to BEAR_TRADITIONAL,
        "unlimited" to UNLIMITED,
        "bear basic" to BEAR_BASIC,
        "bear choice" to BEAR_CHOICE,
        "house meal plan" to HOUSE_MEALPLAN,
        "house affiliate" to HOUSE_AFFILIATE,
        "flex" to FLEX,
        "bucks" to JUST_BUCKS
    )

    val mealPlanTypes = listOf(
        OFF_CAMPUS, BEAR_TRADITIONAL, UNLIMITED, BEAR_BASIC, BEAR_CHOICE, HOUSE_MEALPLAN,
        HOUSE_AFFILIATE, FLEX, JUST_BUCKS)

    // Each of these will have "Meal Plan" appended to it when displayed to the user.
    val mealPlanNameMap = hashMapOf(
        OFF_CAMPUS to "Off-Campus Value",
        BEAR_TRADITIONAL to "Bear Traditional",
        UNLIMITED to "Unlimited",
        BEAR_BASIC to "Bear Basic",
        BEAR_CHOICE to "Bear Choice",
        HOUSE_MEALPLAN to "House",
        HOUSE_AFFILIATE to "House Affiliate",
        FLEX to "Flex 10/500",
        JUST_BUCKS to "Just Bucks"
    )

    val semesterlyMealPlans = listOf(OFF_CAMPUS, FLEX)

    val weeklyMealPlans = listOf(BEAR_TRADITIONAL, BEAR_BASIC, BEAR_CHOICE, HOUSE_AFFILIATE)
}

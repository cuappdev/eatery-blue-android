package com.appdev.eateryblueandroid.util

import android.content.Context
import android.location.Location
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.models.AccountType.*
import com.appdev.eateryblueandroid.ui.screens.settings.Issue
import com.codelab.android.datastore.UserPreferences

object Constants {
    const val BACKEND_URL = "https://eatery-dev.cornellappdev.com/"
    const val GET_BACKEND_URL = "https://services.get.cbord.com/GETServices/services/json/"
    const val SESSIONID_WEBVIEW_URL = "https://get.cbord.com/cornell/full/login.php?mobileapp=1"
    const val SESSIONID_CU_LOGIN_SUBSTRING_IDENTIFIER = "shibidp"
    const val AVERAGE_WALK_SPEED = 1.42
    const val WORLD_DISTANCE_KM = 250000

    // Need these colors for status bar because colors.xml is only accessible from Composables
    val eateryBlueColor = Color((74.0/255).toFloat(), (144.0/255).toFloat(), (226.0/255).toFloat())
    val eateryBlueColorTransparent = Color((74.0/255).toFloat(), (144.0/255).toFloat(), (226.0/255).toFloat(), 0f)

    const val CORNELL_INSTITUTION_ID = "73116ae4-22ad-4c71-8ffd-11ba015407b1"

    const val DATA_STORE_FILE_NAME = "user_prefs.pb"

    var currentLocation: Location? = null

    val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = UserPreferencesSerializer
    )

    const val passwordAlias = "passwordy password"

    /*
    I have no idea if these strings actually correspond to the meal plans as they show up in the GET API. For example,
    You'd expect Bear Traditional to appear as "Bear Traditional" but it shows up as "Traditional Bear" ...?
    TODO: Find out what all the meal plans show up as, and substitute in the correct strings.
    */
    val mealPlanAccountMap: HashMap<String, AccountType> = hashMapOf(
        "off-campus value" to OFF_CAMPUS,
        "traditional" to BEAR_TRADITIONAL,
        "unlimited" to UNLIMITED,
        "basic" to BEAR_BASIC,
        "choice" to BEAR_CHOICE,
        "house meal plan" to HOUSE_MEALPLAN,
        "house affiliate" to HOUSE_AFFILIATE,
        "flex" to FLEX,
        "just bucks" to JUST_BUCKS
    )

    val mealPlanTypes = listOf(
        OFF_CAMPUS, BEAR_TRADITIONAL, UNLIMITED, BEAR_BASIC, BEAR_CHOICE, HOUSE_MEALPLAN,
        HOUSE_AFFILIATE, FLEX, JUST_BUCKS
    )

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

    val issueMap = hashMapOf(
        Issue.ITEM to "Inaccurate or missing item",
        Issue.PRICE to "Different price than listed",
        Issue.HOURS to "Incorrect hours",
        Issue.WAIT_TIMES to "Inaccurate wait times",
        Issue.DESCRIPTION to "Inaccurate description",
        Issue.OTHER to "Other",
        Issue.NONE to "Choose an option..."
    )
}

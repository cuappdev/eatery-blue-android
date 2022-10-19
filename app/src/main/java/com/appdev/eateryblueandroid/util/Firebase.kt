package com.appdev.eateryblueandroid.util

import android.os.Bundle
import com.appdev.eateryblueandroid.ui.analytics
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Logs a successful login to Firebase.
 */
fun logLogin(method: String) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    assert(method.lowercase() == "main" || method.lowercase() == "onboarding")

    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.METHOD, method)
    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
}

/**
 * Logs a successful logout to Firebase.
 */
fun logLogout() {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    analytics.logEvent("logout", Bundle.EMPTY)
}

/**
 * Logs when someone taps on a specific eatery to Firebase.
 */
fun logOpenEatery(id: Int) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    val bundle = Bundle()
    bundle.putInt(FirebaseAnalytics.Param.LOCATION_ID, id)
    analytics.logEvent("openEatery", bundle)
}

/**
 * Logs when a user has completed onboarding to Firebase.
 */
fun logCompletedOnboarding() {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    analytics.logEvent("completedOnboarding", Bundle.EMPTY)
}

/**
 * Logs when an eatery is favorited to Firebase.
 */
fun logFavorite(id: Int) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    val bundle = Bundle()
    bundle.putInt(FirebaseAnalytics.Param.LOCATION_ID, id)
    analytics.logEvent("favorite", bundle)
}

/**
 * Logs an eatery being found by a search to Firebase.
 */
fun logSearch(id: Int) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    val bundle = Bundle()
    bundle.putInt(FirebaseAnalytics.Param.LOCATION_ID, id)
    analytics.logEvent("search", bundle)
}

/**
 * Logs a settings screen being entered to Firebase.
 * @param id ID corresponding to which screen was entered.
 *
 * (0->Main Screen, 1->About, 2->Favorites, 3->Icon,
 * 4->Notifications, 5->Privacy, 6->Legal, 7->Support)
 */
fun logOpenSettingScreen(id: Int) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    val screen = when (id) {
        0 -> "main"
        1 -> "about"
        2 -> "favorites"
        3 -> "icon"
        4 -> "notifications"
        5 -> "privacy"
        6 -> "legal"
        7 -> "support"
        else -> null
    }
    val bundle = Bundle()
    if (screen != null)
        bundle.putString("screen", screen)
    analytics.logEvent("settings", bundle)
}

/**
 * Logs when a user reports an issue to Firebase.
 * @param id ID corresponding to which type of report was entered.
 *
 * (0->Items, 1->Price, 2->Hours, 3->Wait Times, 4-> Description, 5->Other)
 */
fun logReportSend(id: Int) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    val type = when (id) {
        0 -> "items"
        1 -> "price"
        2 -> "hours"
        3 -> "wait"
        4 -> "description"
        5 -> "other"
        else -> null
    }
    val bundle = Bundle()
    if (type != null)
        bundle.putString("type", type)
    analytics.logEvent("report", bundle)
}

/**
 * Logs when the Order Online button is hit for an eatery.
 */
fun logOrderOnline(id : Int) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    val bundle = Bundle()
    bundle.putInt(FirebaseAnalytics.Param.LOCATION_ID, id)
    analytics.logEvent("orderOnline", bundle)
}

/**
 * Logs when the Get Directions button is hit for an eatery.
 */
fun logDirections(id : Int) {
    if (notificationSettingsMap[NotificationsSettingsType.ANALYTICS] == false) return

    val bundle = Bundle()
    bundle.putInt(FirebaseAnalytics.Param.LOCATION_ID, id)
    analytics.logEvent("getDirections", bundle)
}
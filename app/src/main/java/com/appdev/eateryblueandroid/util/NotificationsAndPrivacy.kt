package com.appdev.eateryblueandroid.util

import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.NotificationSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * A map detailing the current set of notification and privacy settings. Use saveNotificationSetting()
 * to set values in the map--do not write directly. Valid keys are: PAUSED, FAVORITE_ITEMS, APPDEV,
 * DINING, ACCOUNT, and ANALYTICS
 */
val notificationSettingsMap: Map<NotificationsSettingsType, Boolean> = hashMapOf(
    NotificationsSettingsType.PAUSED to false,
    NotificationsSettingsType.FAVORITE_ITEMS to true,
    NotificationsSettingsType.APPDEV to true,
    NotificationsSettingsType.DINING to true,
    NotificationsSettingsType.ACCOUNT to true,
    NotificationsSettingsType.ANALYTICS to true
)

fun initializeNotificationsSettings() {
    CoroutineScope(Dispatchers.IO).launch {
        val notificationsFlow: Flow<NotificationSettings> = appContext!!.userPreferencesStore.data
            .map { userPrefs ->
                // The favoritesMap property is generated from the proto schema.
                userPrefs.notificationSettings
            }
        val notificationSettingsMap =
            notificationSettingsMap as MutableMap<NotificationsSettingsType, Boolean>
        notificationsFlow.collect { notifications ->
            notificationSettingsMap[NotificationsSettingsType.PAUSED] = notifications.paused
            notificationSettingsMap[NotificationsSettingsType.FAVORITE_ITEMS] =
                notifications.favoriteItems
            notificationSettingsMap[NotificationsSettingsType.APPDEV] = notifications.cornellAppDev
            notificationSettingsMap[NotificationsSettingsType.DINING] = notifications.cornellDining
            notificationSettingsMap[NotificationsSettingsType.ACCOUNT] = notifications.account
            notificationSettingsMap[NotificationsSettingsType.ANALYTICS] = notifications.analytics

            //Set default values
            if (!notifications.hasSet) {
                saveNotificationSetting(NotificationsSettingsType.PAUSED, false)
                saveNotificationSetting(NotificationsSettingsType.FAVORITE_ITEMS, true)
                saveNotificationSetting(NotificationsSettingsType.APPDEV, true)
                saveNotificationSetting(NotificationsSettingsType.DINING, true)
                saveNotificationSetting(NotificationsSettingsType.ACCOUNT, true)
                saveNotificationSetting(NotificationsSettingsType.ANALYTICS, true)
            }
            this.cancel()
        }
    }
}

enum class NotificationsSettingsType {
    PAUSED, FAVORITE_ITEMS, APPDEV, DINING, ACCOUNT, ANALYTICS
}

fun saveNotificationSetting(type: NotificationsSettingsType, bool: Boolean) {
    val notificationSettingsMap = (notificationSettingsMap as MutableMap<NotificationsSettingsType, Boolean>)
    notificationSettingsMap[type] = bool
    val notificationsSettings : NotificationSettings = NotificationSettings.newBuilder()
        .setPaused(notificationSettingsMap[NotificationsSettingsType.PAUSED]!!)
        .setFavoriteItems(notificationSettingsMap[NotificationsSettingsType.FAVORITE_ITEMS]!!)
        .setCornellAppDev(notificationSettingsMap[NotificationsSettingsType.APPDEV]!!)
        .setCornellDining(notificationSettingsMap[NotificationsSettingsType.DINING]!!)
        .setAccount(notificationSettingsMap[NotificationsSettingsType.ACCOUNT]!!)
        .setAnalytics(notificationSettingsMap[NotificationsSettingsType.ANALYTICS]!!)
        .setHasSet(true)
        .build()

    // Save to proto Datastore
    CoroutineScope(Dispatchers.IO).launch {
        appContext!!.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setNotificationSettings(notificationsSettings)
                .build()
        }
    }
}

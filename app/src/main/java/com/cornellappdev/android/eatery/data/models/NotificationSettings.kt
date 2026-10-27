package com.cornellappdev.android.eatery.data.models

/**
 * The user's notification settings as stored on this device. [allEnabled] is false while
 * notifications are paused in-app; the category flags keep the user's choices so unpausing can
 * restore them.
 */
data class NotificationSettings(
    val allEnabled: Boolean,
    val favoriteItemsEnabled: Boolean,
    val appDevEnabled: Boolean,
    /** Whether POST_NOTIFICATIONS has been requested, so a denial reads as turned off. */
    val permissionRequested: Boolean,
)

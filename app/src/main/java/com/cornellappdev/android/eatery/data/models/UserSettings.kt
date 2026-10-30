package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.JsonClass

/**
 * The user's server-side notification settings, as returned by `GET /users/settings` and
 * `PATCH /users/settings`. Users who never changed a setting get the backend's defaults.
 */
@JsonClass(generateAdapter = true)
data class UserSettings(
    val favoriteItemPushNotifications: Boolean,
    val cornellAppdevPushNotifications: Boolean,
)

/**
 * Request body for `PATCH /users/settings`. Only non-null fields are sent; the backend rejects
 * unknown fields and requires at least one setting.
 */
@JsonClass(generateAdapter = true)
data class UserSettingsUpdate(
    val favoriteItemPushNotifications: Boolean? = null,
    val cornellAppdevPushNotifications: Boolean? = null,
)

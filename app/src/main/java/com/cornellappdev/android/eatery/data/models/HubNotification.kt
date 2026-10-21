package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.JsonClass

/**
 * An entry in the notification hub. [title] and [body] are composed by the backend and shown
 * as-is.
 */
@JsonClass(generateAdapter = true)
data class HubNotification(
    val id: Int,
    val title: String,
    val body: String,
    val isRead: Boolean,
)

@JsonClass(generateAdapter = true)
data class HubNotificationsResponse(
    val notifications: List<HubNotification> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class NotificationIds(
    val ids: List<Int>,
)

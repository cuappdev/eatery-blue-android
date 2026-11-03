package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.JsonClass

/**
 * An entry in the notification hub. [body] is composed by the backend and may bold spans with
 * `**…**`.
 */
@JsonClass(generateAdapter = true)
data class HubNotification(
    val id: Int,
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

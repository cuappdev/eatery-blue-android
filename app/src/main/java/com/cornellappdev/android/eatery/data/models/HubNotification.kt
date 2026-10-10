package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

/**
 * A single entry in the notification hub, as returned by `GET /users/notifications`.
 *
 * [title] and [body] arrive pre-composed by the backend, so they are displayed as-is rather
 * than being reassembled from structured item/eatery fields.
 */
@JsonClass(generateAdapter = true)
data class HubNotification(
    val id: Int,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime? = null,
)

@JsonClass(generateAdapter = true)
data class HubNotificationsResponse(
    val notifications: List<HubNotification> = emptyList(),
)

/**
 * Request body for marking notifications read and for deleting them.
 */
@JsonClass(generateAdapter = true)
data class NotificationIds(
    val ids: List<Int>,
)

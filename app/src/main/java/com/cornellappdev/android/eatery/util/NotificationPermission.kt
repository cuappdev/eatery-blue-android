package com.cornellappdev.android.eatery.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException

private const val LOG_TAG = "NotificationPermission"

fun needsNotificationPermissionRequest(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return false
    }

    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
    ) != PackageManager.PERMISSION_GRANTED
}

/**
 * Whether the OS will actually surface a notification. On API 33+ this reflects the
 * POST_NOTIFICATIONS grant; below that it reflects the user's system-settings toggle, which
 * [needsNotificationPermissionRequest] cannot see.
 */
fun areNotificationsAllowedBySystem(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()

/**
 * Reads a stored preference, falling back to [default] when DataStore can't be read rather than
 * propagating the IOException to a caller that has no way to recover from it.
 */
suspend fun Flow<Boolean>.firstOrOnReadFailure(default: Boolean): Boolean =
    try {
        first()
    } catch (e: IOException) {
        Log.w(LOG_TAG, "Failed to read a notification preference", e)
        default
    }

suspend fun shouldRequestNotificationPermission(
    context: Context,
    notificationsEnabledFlow: Flow<Boolean>,
): Boolean {
    return notificationsEnabledFlow.firstOrOnReadFailure(false) &&
            needsNotificationPermissionRequest(context)
}

suspend fun canGetNotifications(
    context: Context,
    notificationsEnabledFlow: Flow<Boolean>,
): Boolean {
    return notificationsEnabledFlow.firstOrOnReadFailure(false) &&
            areNotificationsAllowedBySystem(context)
}

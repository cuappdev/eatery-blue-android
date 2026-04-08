package com.cornellappdev.android.eatery.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

fun needsNotificationPermissionRequest(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return false
    }

    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
    ) != PackageManager.PERMISSION_GRANTED
}

suspend fun shouldRequestNotificationPermission(
    context: Context,
    notificationsEnabledFlow: Flow<Boolean>,
): Boolean {
    return notificationsEnabledFlow.first() && needsNotificationPermissionRequest(context)
}

suspend fun canGetNotifications(
    context: Context,
    notificationsEnabledFlow: Flow<Boolean>,
): Boolean {
    return notificationsEnabledFlow.first() && !needsNotificationPermissionRequest(context)
}
package com.cornellappdev.android.eatery.notifications

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads the OS-level notification permission state for the app.
 */
@Singleton
class NotificationPermissionChecker @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    /** Whether POST_NOTIFICATIONS still needs to be requested. Always false below API 33. */
    fun needsPermissionRequest(): Boolean {
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
     * [needsPermissionRequest] cannot see.
     */
    fun areNotificationsAllowedBySystem(): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
}

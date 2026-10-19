package com.cornellappdev.android.eatery.data.repositories

import android.content.Context
import android.util.Log
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.FcmToken
import com.cornellappdev.android.eatery.data.models.HubNotification
import com.cornellappdev.android.eatery.data.models.NotificationIds
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.UserSettings
import com.cornellappdev.android.eatery.data.models.UserSettingsUpdate
import com.cornellappdev.android.eatery.util.areNotificationsAllowedBySystem
import com.cornellappdev.android.eatery.util.firstOrOnReadFailure
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Push registration, the notification hub and notification settings. The server is the source of
 * truth for hub read state, so local state is only updated once a request succeeds.
 */
@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    companion object {
        private const val LOG_TAG = "NotificationRepository"
    }

    private val _notificationsFlow: MutableStateFlow<List<HubNotification>> =
        MutableStateFlow(emptyList())

    /**
     * The user's notifications from the last 24 hours, most recent first.
     */
    val notificationsFlow: StateFlow<List<HubNotification>> = _notificationsFlow.asStateFlow()

    /**
     * Registers [token] whenever the OS allows notifications, even while paused in-app: the
     * backend keeps tokens regardless of settings, so unpausing works without re-registering.
     */
    suspend fun registerFcmToken(token: String) {
        if (!areNotificationsAllowedBySystem(context)) return

        val result = resultOfNetworkCall { networkApi.enableNotifications(FcmToken(token)) }
        if (result is Result.Error) {
            Log.w(LOG_TAG, "Failed to register FCM token: ${result.error}")
        }
    }

    /** Whether a push should be shown: allowed by the OS and not paused in-app. */
    suspend fun canShowNotifications(): Boolean =
        userPreferencesRepository.notificationsEnabledFlow.firstOrOnReadFailure(false) &&
                areNotificationsAllowedBySystem(context)

    suspend fun fetchNotifications(): Result<Unit> = resultOfNetworkCall {
        _notificationsFlow.value = networkApi.getNotifications().notifications
    }

    suspend fun markAsRead(ids: List<Int>): Result<Unit> = resultOfNetworkCall {
        networkApi.markNotificationsRead(NotificationIds(ids))
        _notificationsFlow.update { notifications ->
            notifications.map { if (it.id in ids) it.copy(isRead = true) else it }
        }
    }

    suspend fun getSettings(): Result<UserSettings> = resultOfNetworkCall {
        networkApi.getSettings()
    }

    suspend fun updateSettings(update: UserSettingsUpdate): Result<UserSettings> =
        resultOfNetworkCall {
            networkApi.updateSettings(update)
        }
}

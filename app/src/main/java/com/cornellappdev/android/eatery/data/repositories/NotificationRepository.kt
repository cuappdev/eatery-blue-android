package com.cornellappdev.android.eatery.data.repositories

import android.content.Context
import android.util.Log
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.FcmToken
import com.cornellappdev.android.eatery.data.models.HubNotification
import com.cornellappdev.android.eatery.data.models.NetworkError
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val registerTokenMutex = Mutex()

    /** The token last registered successfully in this process, so repeat calls are skipped. */
    private var registeredFcmToken: String? = null

    /**
     * Registers [token] whenever the OS allows notifications, even while paused in-app: the
     * backend keeps tokens regardless of settings, so unpausing works without re-registering.
     * Startup and resume can both ask at once, so an already-registered token is skipped.
     */
    suspend fun registerFcmToken(token: String) {
        if (!areNotificationsAllowedBySystem(context)) return

        registerTokenMutex.withLock {
            if (token == registeredFcmToken) return

            val result = resultOfNetworkCall { networkApi.enableNotifications(FcmToken(token)) }
            if (result is Result.Error) {
                Log.w(LOG_TAG, "Failed to register FCM token: ${result.error}")
            } else {
                registeredFcmToken = token
            }
        }
    }

    /** Whether a push should be shown: allowed by the OS and not paused in-app. */
    suspend fun canShowNotifications(): Boolean =
        userPreferencesRepository.notificationsEnabledFlow.firstOrOnReadFailure(false) &&
                areNotificationsAllowedBySystem(context)

    private val _hubAvailableFlow = MutableStateFlow(true)

    /**
     * False once the backend answers the hub with a 404, meaning it hasn't been deployed with
     * notification support yet. Other failures (e.g. offline) leave it unchanged.
     */
    val hubAvailableFlow: StateFlow<Boolean> = _hubAvailableFlow.asStateFlow()

    suspend fun fetchNotifications(): Result<Unit> {
        val result = resultOfNetworkCall {
            _notificationsFlow.value = networkApi.getNotifications().notifications
        }
        when (result) {
            is Result.Success -> _hubAvailableFlow.value = true
            is Result.Error -> {
                val error = result.error
                if (error is NetworkError.ServerError && error.code == 404) {
                    _hubAvailableFlow.value = false
                }
            }
        }
        return result
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

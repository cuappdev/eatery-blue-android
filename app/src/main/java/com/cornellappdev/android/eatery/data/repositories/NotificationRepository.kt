package com.cornellappdev.android.eatery.data.repositories

import android.util.Log
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.FcmToken
import com.cornellappdev.android.eatery.data.models.HubNotification
import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.data.models.NotificationIds
import com.cornellappdev.android.eatery.data.models.NotificationSettings
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.UserSettingsUpdate
import com.cornellappdev.android.eatery.notifications.NotificationPermissionChecker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Push registration, the notification hub and notification settings. The server is the source of
 * truth for hub read state, so local state is only updated once a request succeeds.
 */
@Singleton
class NotificationRepository @Inject constructor(
    private val permissionChecker: NotificationPermissionChecker,
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authTokenRepository: AuthTokenRepository,
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
        if (!permissionChecker.areNotificationsAllowedBySystem()) return

        registerTokenMutex.withLock {
            if (token == registeredFcmToken) return

            val result = authTokenRepository.authorizedNetworkCall {
                networkApi.enableNotifications(FcmToken(token))
            }
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
                permissionChecker.areNotificationsAllowedBySystem()

    private val _hubAvailableFlow = MutableStateFlow(true)

    /**
     * False once the backend answers the hub with a 404, meaning it hasn't been deployed with
     * notification support yet. Other failures (e.g. offline) leave it unchanged.
     */
    val hubAvailableFlow: StateFlow<Boolean> = _hubAvailableFlow.asStateFlow()

    suspend fun fetchNotifications(): Result<Unit> {
        val result = authTokenRepository.authorizedNetworkCall {
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

    suspend fun markAsRead(ids: List<Int>): Result<Unit> = authTokenRepository.authorizedNetworkCall {
        networkApi.markNotificationsRead(NotificationIds(ids))
        _notificationsFlow.update { notifications ->
            notifications.map { if (it.id in ids) it.copy(isRead = true) else it }
        }
    }

    /** Serializes settings requests and backend loads. */
    private val settingsMutex = Mutex()

    private enum class Setting { ALL, FAVORITE_ITEMS, APP_DEV }

    /** Source of ids for settings updates. */
    private val settingsUpdateCount = AtomicLong()

    /** Settings updates saved locally whose request hasn't finished yet. */
    private val inFlightSettingsUpdates = AtomicInteger()

    /** The id of the newest update to each setting; older queued updates are superseded. */
    private val latestSettingUpdates = ConcurrentHashMap<Setting, Long>()

    /** The user's notification settings as stored on this device. */
    val settingsFlow: Flow<NotificationSettings> = combine(
        userPreferencesRepository.notificationsEnabledFlow,
        userPreferencesRepository.favoriteItemNotificationsEnabledFlow,
        userPreferencesRepository.appDevNotificationsEnabledFlow,
        userPreferencesRepository.notificationPermissionRequestedFlow,
    ) { allEnabled, favoriteItemsEnabled, appDevEnabled, permissionRequested ->
        NotificationSettings(
            allEnabled = allEnabled,
            favoriteItemsEnabled = favoriteItemsEnabled,
            appDevEnabled = appDevEnabled,
            permissionRequested = permissionRequested,
        )
    }

    /**
     * Overwrites the local category settings with the backend's, which decide what gets sent.
     */
    suspend fun loadSettings(): Result<Unit> = settingsMutex.withLock {
        // While paused the backend has every category turned off, so its values aren't the
        // user's real choices; keep the local ones so they can be restored on unpause.
        if (!userPreferencesRepository.notificationsEnabledFlow.firstOrOnReadFailure(true)) {
            return Result.Success(Unit)
        }

        when (val result = authTokenRepository.authorizedNetworkCall { networkApi.getSettings() }) {
            is Result.Success -> {
                // A toggle that's already shown but not yet sent is newer than the backend's
                // values, so don't overwrite it.
                if (inFlightSettingsUpdates.get() == 0) {
                    userPreferencesRepository.setFavoriteItemNotificationsEnabled(
                        result.data.favoriteItemPushNotifications
                    )
                    userPreferencesRepository.setAppDevNotificationsEnabled(
                        result.data.cornellAppdevPushNotifications
                    )
                }
                Result.Success(Unit)
            }

            is Result.Error -> {
                Log.w(LOG_TAG, "Failed to load settings: ${result.error}")
                result
            }
        }
    }

    /** Records the permission prompt so a later denial is treated as notifications turned off. */
    suspend fun markPermissionRequested() {
        userPreferencesRepository.setNotificationPermissionRequested(true)
    }

    /**
     * The backend has no master switch, so pausing turns off every category there and unpausing
     * restores the user's local category choices. The FCM token stays registered either way.
     */
    suspend fun setAllNotificationsEnabled(enabled: Boolean): Result<Unit> {
        val update = if (enabled) {
            UserSettingsUpdate(
                favoriteItemPushNotifications = userPreferencesRepository
                    .favoriteItemNotificationsEnabledFlow.firstOrOnReadFailure(true),
                cornellAppdevPushNotifications = userPreferencesRepository
                    .appDevNotificationsEnabledFlow.firstOrOnReadFailure(true),
            )
        } else {
            UserSettingsUpdate(
                favoriteItemPushNotifications = false,
                cornellAppdevPushNotifications = false,
            )
        }
        return updateSetting(
            Setting.ALL,
            enabled,
            update,
            userPreferencesRepository::setNotificationsEnabled,
        )
    }

    suspend fun setFavoriteItemNotificationsEnabled(enabled: Boolean): Result<Unit> =
        updateSetting(
            Setting.FAVORITE_ITEMS,
            enabled,
            UserSettingsUpdate(favoriteItemPushNotifications = enabled),
            userPreferencesRepository::setFavoriteItemNotificationsEnabled,
        )

    suspend fun setAppDevNotificationsEnabled(enabled: Boolean): Result<Unit> =
        updateSetting(
            Setting.APP_DEV,
            enabled,
            UserSettingsUpdate(cornellAppdevPushNotifications = enabled),
            userPreferencesRepository::setAppDevNotificationsEnabled,
        )

    /**
     * Saves [enabled] locally right away so the UI updates on tap, then syncs [update] to the
     * backend under [settingsMutex] so requests go out in tap order. If the [setting] is toggled
     * again before this one is sent, only the newest is sent; if a request fails, the local value
     * is reverted unless a newer toggle has already replaced it.
     */
    private suspend fun updateSetting(
        setting: Setting,
        enabled: Boolean,
        update: UserSettingsUpdate,
        setLocal: suspend (Boolean) -> Unit,
    ): Result<Unit> {
        inFlightSettingsUpdates.incrementAndGet()
        try {
            val updateId = settingsUpdateCount.incrementAndGet()
            latestSettingUpdates[setting] = updateId
            setLocal(enabled)

            return settingsMutex.withLock {
                if (latestSettingUpdates[setting] != updateId) return Result.Success(Unit)

                when (val result = authTokenRepository.authorizedNetworkCall {
                    networkApi.updateSettings(update)
                }) {
                    is Result.Success -> Result.Success(Unit)
                    is Result.Error -> {
                        Log.w(LOG_TAG, "Failed to sync notification setting: ${result.error}")
                        if (latestSettingUpdates[setting] != updateId) return Result.Success(Unit)
                        setLocal(!enabled)
                        result
                    }
                }
            }
        } finally {
            inFlightSettingsUpdates.decrementAndGet()
        }
    }
}

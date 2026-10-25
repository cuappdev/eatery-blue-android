package com.cornellappdev.android.eatery.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.UserSettingsUpdate
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.NotificationRepository
import com.cornellappdev.android.eatery.util.firstOrOnReadFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

data class NotificationsSettingsUiState(
    val allNotificationsEnabled: Boolean = true,
    val favoriteItemNotificationsEnabled: Boolean = true,
    val appDevNotificationsEnabled: Boolean = true,
    val permissionRequested: Boolean = false,
    /** The backend couldn't be reached, so the local values may not be what it will send. */
    val settingsLoadFailed: Boolean = false,
)

@HiltViewModel
class NotificationsSettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    companion object {
        private const val LOG_TAG = "NotificationsSettingsVM"
    }

    private val _syncErrorFlow = MutableSharedFlow<Unit>()
    val syncErrorFlow = _syncErrorFlow.asSharedFlow()

    private val settingsLoadFailed = MutableStateFlow(false)

    /** Serializes settings updates and backend loads. */
    private val settingsMutex = Mutex()

    val uiState: StateFlow<NotificationsSettingsUiState> = combine(
        userPreferencesRepository.notificationsEnabledFlow,
        userPreferencesRepository.favoriteItemNotificationsEnabledFlow,
        userPreferencesRepository.appDevNotificationsEnabledFlow,
        userPreferencesRepository.notificationPermissionRequestedFlow,
        settingsLoadFailed,
    ) { allNotificationsEnabled, favoriteItemEnabled, appDevEnabled, permissionRequested, loadFailed ->
        NotificationsSettingsUiState(
            allNotificationsEnabled = allNotificationsEnabled,
            favoriteItemNotificationsEnabled = favoriteItemEnabled,
            appDevNotificationsEnabled = appDevEnabled,
            permissionRequested = permissionRequested,
            settingsLoadFailed = loadFailed,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NotificationsSettingsUiState(),
    )

    /**
     * Overwrites the local category settings with the backend's, which decide what gets sent.
     */
    fun loadSettingsFromBackend() = viewModelScope.launch {
        settingsMutex.withLock {
            // While paused the backend has every category turned off, so its values aren't the
            // user's real choices; keep the local ones so they can be restored on unpause.
            if (!userPreferencesRepository.notificationsEnabledFlow.firstOrOnReadFailure(true)) {
                settingsLoadFailed.value = false
                return@launch
            }

            when (val result = notificationRepository.getSettings()) {
                is Result.Success -> {
                    userPreferencesRepository.setFavoriteItemNotificationsEnabled(
                        result.data.favoriteItemPushNotifications
                    )
                    userPreferencesRepository.setAppDevNotificationsEnabled(
                        result.data.cornellAppdevPushNotifications
                    )
                    settingsLoadFailed.value = false
                }

                is Result.Error -> {
                    Log.w(LOG_TAG, "Failed to load settings: ${result.error}")
                    settingsLoadFailed.value = true
                }
            }
        }
    }

    /** Records the permission prompt so a later denial is treated as notifications turned off. */
    fun markPermissionRequested() = viewModelScope.launch {
        userPreferencesRepository.setNotificationPermissionRequested(true)
    }

    /**
     * The backend has no master switch, so pausing turns off every category there and unpausing
     * restores the user's local category choices. The FCM token stays registered either way.
     */
    fun setAllNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
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
        updateSetting(enabled, update, userPreferencesRepository::setNotificationsEnabled)
    }

    fun setFavoriteItemNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        updateSetting(
            enabled,
            UserSettingsUpdate(favoriteItemPushNotifications = enabled),
            userPreferencesRepository::setFavoriteItemNotificationsEnabled,
        )
    }

    fun setAppDevNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        updateSetting(
            enabled,
            UserSettingsUpdate(cornellAppdevPushNotifications = enabled),
            userPreferencesRepository::setAppDevNotificationsEnabled,
        )
    }

    /**
     * Saves [enabled] locally first so the UI updates immediately, then syncs [update] to the
     * backend, reverting the local value if that fails. Runs under [settingsMutex] so updates reach
     * the backend in tap order and a resume-triggered load can't overwrite one in flight.
     */
    private suspend fun updateSetting(
        enabled: Boolean,
        update: UserSettingsUpdate,
        setLocal: suspend (Boolean) -> Unit,
    ) = settingsMutex.withLock {
        setLocal(enabled)

        val result = notificationRepository.updateSettings(update)
        if (result is Result.Error) {
            Log.w(LOG_TAG, "Failed to sync notification setting: ${result.error}")
            _syncErrorFlow.emit(Unit)
            setLocal(!enabled)
        }
    }
}

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsSettingsUiState(
    val allNotificationsEnabled: Boolean = true,
    val favoriteItemNotificationsEnabled: Boolean = true,
    val cornellAppdevNotificationsEnabled: Boolean = true,
    val permissionRequested: Boolean = false,
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

    val uiState: StateFlow<NotificationsSettingsUiState> = combine(
        userPreferencesRepository.notificationsEnabledFlow,
        userPreferencesRepository.favoriteItemNotificationsEnabledFlow,
        userPreferencesRepository.cornellAppdevNotificationsEnabledFlow,
        userPreferencesRepository.notificationPermissionRequestedFlow,
    ) { allNotificationsEnabled, favoriteItemEnabled, cornellAppdevEnabled, permissionRequested ->
        NotificationsSettingsUiState(
            allNotificationsEnabled = allNotificationsEnabled,
            favoriteItemNotificationsEnabled = favoriteItemEnabled,
            cornellAppdevNotificationsEnabled = cornellAppdevEnabled,
            permissionRequested = permissionRequested,
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
        // While paused the backend has every category turned off, so its values aren't the
        // user's real choices; keep the local ones so they can be restored on unpause.
        if (!userPreferencesRepository.notificationsEnabledFlow.firstOrOnReadFailure(true)) {
            return@launch
        }

        when (val result = notificationRepository.getSettings()) {
            is Result.Success -> {
                userPreferencesRepository.setFavoriteItemNotificationsEnabled(
                    result.data.favoriteItemPushNotifications
                )
                userPreferencesRepository.setCornellAppdevNotificationsEnabled(
                    result.data.cornellAppdevPushNotifications
                )
            }

            is Result.Error -> Log.w(LOG_TAG, "Failed to load settings: ${result.error}")
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
                    .cornellAppdevNotificationsEnabledFlow.firstOrOnReadFailure(true),
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

    fun setCornellAppdevNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        updateSetting(
            enabled,
            UserSettingsUpdate(cornellAppdevPushNotifications = enabled),
            userPreferencesRepository::setCornellAppdevNotificationsEnabled,
        )
    }

    /**
     * Saves [enabled] locally first so the UI updates immediately, then syncs [update] to the
     * backend, reverting the local value if that fails.
     */
    private suspend fun updateSetting(
        enabled: Boolean,
        update: UserSettingsUpdate,
        setLocal: suspend (Boolean) -> Unit,
    ) {
        setLocal(enabled)

        val result = notificationRepository.updateSettings(update)
        if (result is Result.Error) {
            Log.w(LOG_TAG, "Failed to sync notification setting: ${result.error}")
            _syncErrorFlow.emit(Unit)
            setLocal(!enabled)
        }
    }
}

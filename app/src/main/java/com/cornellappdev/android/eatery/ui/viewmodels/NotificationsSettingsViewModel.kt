package com.cornellappdev.android.eatery.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.UserSettingsUpdate
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
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
)

@HiltViewModel
class NotificationsSettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    companion object {
        private const val LOG_TAG = "NotificationsSettingsVM"
    }

    private val _syncErrorFlow = MutableSharedFlow<String>()
    val syncErrorFlow = _syncErrorFlow.asSharedFlow()

    val uiState: StateFlow<NotificationsSettingsUiState> = combine(
        userPreferencesRepository.notificationsEnabledFlow,
        userPreferencesRepository.favoriteItemNotificationsEnabledFlow,
        userPreferencesRepository.cornellAppdevNotificationsEnabledFlow,
    ) { allNotificationsEnabled, favoriteItemEnabled, cornellAppdevEnabled ->
        NotificationsSettingsUiState(
            allNotificationsEnabled = allNotificationsEnabled,
            favoriteItemNotificationsEnabled = favoriteItemEnabled,
            cornellAppdevNotificationsEnabled = cornellAppdevEnabled,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NotificationsSettingsUiState(),
    )

    init {
        loadSettingsFromBackend()
    }

    /**
     * The backend is the source of truth for the per-category settings, since it decides whether
     * to send those pushes, so the local preferences are overwritten with the server's values.
     */
    private fun loadSettingsFromBackend() = viewModelScope.launch {
        // While paused the backend has every category turned off, so its values aren't the
        // user's real choices; keep the local ones so they can be restored on unpause.
        if (!userPreferencesRepository.notificationsEnabledFlow.firstOrOnReadFailure(true)) {
            return@launch
        }

        when (val result = userRepository.getSettings()) {
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

    /**
     * The backend has no master switch, so pausing turns off every category there and unpausing
     * restores the user's local category choices. The FCM token stays registered either way.
     */
    fun setAllNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        // enable first so that UI updates immediately, then sync with backend
        userPreferencesRepository.setNotificationsEnabled(enabled)

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

        val result = userRepository.updateSettings(update)
        if (result is Result.Error) {
            Log.w(LOG_TAG, "Failed to sync notification setting: ${result.error}")
            _syncErrorFlow.emit("Failed to update notifications: ${result.error}")
            userPreferencesRepository.setNotificationsEnabled(!enabled)
        }
    }

    fun setFavoriteItemNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        // enable first so that UI updates immediately, then sync with backend
        userPreferencesRepository.setFavoriteItemNotificationsEnabled(enabled)

        val result = userRepository.updateSettings(
            UserSettingsUpdate(favoriteItemPushNotifications = enabled)
        )
        if (result is Result.Error) {
            Log.w(LOG_TAG, "Failed to sync favorite item setting: ${result.error}")
            _syncErrorFlow.emit("Failed to update notifications: ${result.error}")
            userPreferencesRepository.setFavoriteItemNotificationsEnabled(!enabled)
        }
    }

    fun setCornellAppdevNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        // enable first so that UI updates immediately, then sync with backend
        userPreferencesRepository.setCornellAppdevNotificationsEnabled(enabled)

        val result = userRepository.updateSettings(
            UserSettingsUpdate(cornellAppdevPushNotifications = enabled)
        )
        if (result is Result.Error) {
            Log.w(LOG_TAG, "Failed to sync Cornell AppDev setting: ${result.error}")
            _syncErrorFlow.emit("Failed to update notifications: ${result.error}")
            userPreferencesRepository.setCornellAppdevNotificationsEnabled(!enabled)
        }
    }
}

package com.cornellappdev.android.eatery.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.UserSettingsUpdate
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
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

    fun setAllNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setNotificationsEnabled(enabled)
    }

    fun syncNotificationSettingsWithBackend(enabled: Boolean, token: String?) =
        viewModelScope.launch {
            if (token.isNullOrBlank()) {
                // Without a token the backend can't be told anything, so the local preference
                // would drift out of sync with what the server still sends to this device.
                Log.w(LOG_TAG, "Cannot sync notification setting: no FCM token")
                _syncErrorFlow.emit("Failed to update notifications: no device token")
                userPreferencesRepository.setNotificationsEnabled(!enabled)
                return@launch
            }

            val result = if (enabled) {
                userRepository.enableNotifications(token)
            } else {
                userRepository.disableNotifications(token)
            }

            if (result is Result.Error) {
                Log.w(LOG_TAG, "Failed to sync notification setting: ${result.error}")
                val errorMsg = "Failed to update notifications: ${result.error}"
                _syncErrorFlow.emit(errorMsg)
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

package com.cornellappdev.android.eatery.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Result
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
    val favoriteEateryOpeningNotificationsEnabled: Boolean = true,
    val favoriteEateryClosingNotificationsEnabled: Boolean = true,
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
        userPreferencesRepository.favoriteEateryOpeningNotificationsEnabledFlow,
        userPreferencesRepository.favoriteEateryClosingNotificationsEnabledFlow,
    ) { allNotificationsEnabled, favoriteItemEnabled, favoriteEateryOpeningEnabled, favoriteEateryClosingEnabled ->
        NotificationsSettingsUiState(
            allNotificationsEnabled = allNotificationsEnabled,
            favoriteItemNotificationsEnabled = favoriteItemEnabled,
            favoriteEateryOpeningNotificationsEnabled = favoriteEateryOpeningEnabled,
            favoriteEateryClosingNotificationsEnabled = favoriteEateryClosingEnabled,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NotificationsSettingsUiState(),
    )

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

    // TODO: the three per-category preferences below are stored locally only. The backend has no
    // per-category endpoint yet, so it sends favorite-item pushes regardless of these, and since
    // they arrive while the app is backgrounded the OS displays them before any local check runs.
    // Disabling a category therefore has no effect until the backend can filter on it.
    fun setFavoriteItemNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setFavoriteItemNotificationsEnabled(enabled)
    }

    fun setFavoriteEateryOpeningNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setFavoriteEateryOpeningNotificationsEnabled(enabled)
    }

    fun setFavoriteEateryClosingNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setFavoriteEateryClosingNotificationsEnabled(enabled)
    }
}



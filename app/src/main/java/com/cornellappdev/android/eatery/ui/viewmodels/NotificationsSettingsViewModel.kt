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
        userPreferencesRepository.setFavoriteItemNotificationsEnabled(enabled)
    }

    fun setFavoriteEateryOpeningNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setFavoriteEateryOpeningNotificationsEnabled(enabled)
    }

    fun setFavoriteEateryClosingNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setFavoriteEateryClosingNotificationsEnabled(enabled)
    }
}



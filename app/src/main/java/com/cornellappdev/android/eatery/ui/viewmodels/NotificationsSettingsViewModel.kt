package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.NotificationRepository
import com.cornellappdev.android.eatery.notifications.NotificationPermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    private val notificationRepository: NotificationRepository,
    private val permissionChecker: NotificationPermissionChecker,
) : ViewModel() {

    private val _systemNotificationsAllowed =
        MutableStateFlow(permissionChecker.areNotificationsAllowedBySystem())

    /** Whether the OS allows notifications; see [refreshSystemNotificationsAllowed]. */
    val systemNotificationsAllowed: StateFlow<Boolean> = _systemNotificationsAllowed.asStateFlow()

    /** Re-reads the OS permission, which can change in system settings or via the prompt. */
    fun refreshSystemNotificationsAllowed() {
        _systemNotificationsAllowed.value = permissionChecker.areNotificationsAllowedBySystem()
    }

    fun needsNotificationPermissionRequest(): Boolean = permissionChecker.needsPermissionRequest()

    private val _syncErrorFlow = MutableSharedFlow<Unit>()

    /** Emits when a settings change couldn't reach the backend and was reverted. */
    val syncErrorFlow = _syncErrorFlow.asSharedFlow()

    private val settingsLoadFailed = MutableStateFlow(false)

    val uiState: StateFlow<NotificationsSettingsUiState> = combine(
        notificationRepository.settingsFlow,
        settingsLoadFailed,
    ) { settings, loadFailed ->
        NotificationsSettingsUiState(
            allNotificationsEnabled = settings.allEnabled,
            favoriteItemNotificationsEnabled = settings.favoriteItemsEnabled,
            appDevNotificationsEnabled = settings.appDevEnabled,
            permissionRequested = settings.permissionRequested,
            settingsLoadFailed = loadFailed,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NotificationsSettingsUiState(),
    )

    fun loadSettingsFromBackend() = viewModelScope.launch {
        settingsLoadFailed.value = notificationRepository.loadSettings() is Result.Error
    }

    fun markPermissionRequested() = viewModelScope.launch {
        notificationRepository.markPermissionRequested()
    }

    fun setAllNotificationsEnabled(enabled: Boolean) =
        updateSetting { notificationRepository.setAllNotificationsEnabled(enabled) }

    fun setFavoriteItemNotificationsEnabled(enabled: Boolean) =
        updateSetting { notificationRepository.setFavoriteItemNotificationsEnabled(enabled) }

    fun setAppDevNotificationsEnabled(enabled: Boolean) =
        updateSetting { notificationRepository.setAppDevNotificationsEnabled(enabled) }

    /** Runs [update] and shows the sync error snackbar if the repository had to revert it. */
    private fun updateSetting(update: suspend () -> Result<Unit>) = viewModelScope.launch {
        if (update() is Result.Error) {
            _syncErrorFlow.emit(Unit)
        }
    }
}

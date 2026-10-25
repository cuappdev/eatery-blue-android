package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.notifications.NotificationPermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val permissionChecker: NotificationPermissionChecker,
) : ViewModel() {

    val analyticsDisabledFlow: StateFlow<Boolean> =
        userPreferencesRepository.analyticsDisabledFlow
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _notificationsAllowed =
        MutableStateFlow(permissionChecker.areNotificationsAllowedBySystem())

    /** Whether the OS allows notifications; see [refreshNotificationsAllowed]. */
    val notificationsAllowed: StateFlow<Boolean> = _notificationsAllowed.asStateFlow()

    /** Re-reads the OS permission, since the user may have changed it in system settings. */
    fun refreshNotificationsAllowed() {
        _notificationsAllowed.value = permissionChecker.areNotificationsAllowedBySystem()
    }

    fun setAnalyticsDisabled(analyticsDisabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setAnalyticsDisabled(analyticsDisabled)
    }
}

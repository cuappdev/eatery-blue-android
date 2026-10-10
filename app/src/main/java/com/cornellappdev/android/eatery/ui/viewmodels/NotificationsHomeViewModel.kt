package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.HubNotification
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotificationsHomeViewState {
    data class Loaded(val notifications: List<HubNotification>) : NotificationsHomeViewState()
    data object Loading : NotificationsHomeViewState()
    data object Error : NotificationsHomeViewState()
}

@HiltViewModel
class NotificationsHomeViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val hasError = MutableStateFlow(false)

    val uiState: StateFlow<NotificationsHomeViewState> = combine(
        notificationRepository.notificationsFlow,
        isLoading,
        hasError,
    ) { notifications, loading, error ->
        when {
            // Home already refreshes the hub, so cached notifications are usually available by
            // the time this screen opens. Showing them beats flashing a loading or error state
            // over data we already have; the refresh then happens silently underneath.
            notifications.isNotEmpty() -> NotificationsHomeViewState.Loaded(notifications)
            loading -> NotificationsHomeViewState.Loading
            error -> NotificationsHomeViewState.Error
            else -> NotificationsHomeViewState.Loaded(emptyList())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        NotificationsHomeViewState.Loading,
    )

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        hasError.value = false
        isLoading.value = true

        if (notificationRepository.fetchNotifications() is Result.Error) {
            hasError.value = true
        }

        isLoading.value = false
    }

    fun markAsRead(id: Int) = viewModelScope.launch {
        notificationRepository.markAsRead(listOf(id))
    }
}

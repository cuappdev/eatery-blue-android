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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    /**
     * Notifications that were unread when this visit fetched them. They're marked read on the
     * server right away (clearing the bell's dot), but keep their unread styling until the user
     * leaves the hub so they can still see what's new.
     */
    private val unreadThisVisit = MutableStateFlow<Set<Int>>(emptySet())

    private val _isRefreshing = MutableStateFlow(false)

    /** True only while a pull-to-refresh is in flight, so the initial load keeps its shimmer. */
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<NotificationsHomeViewState> = combine(
        notificationRepository.notificationsFlow,
        unreadThisVisit,
        isLoading,
        hasError,
    ) { fetchedNotifications, unreadIds, loading, error ->
        val notifications = fetchedNotifications.map {
            if (it.id in unreadIds) it.copy(isRead = false) else it
        }
        when {
            // Cached notifications (fetched by Home) take priority over loading/error states.
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
        } else {
            markUnreadAsRead()
        }

        isLoading.value = false
    }

    /** Keeps the current list on screen; a failure leaves it unchanged. */
    fun pullToRefresh() = viewModelScope.launch {
        _isRefreshing.value = true
        if (notificationRepository.fetchNotifications() is Result.Success) {
            markUnreadAsRead()
        }
        _isRefreshing.value = false
    }

    /** Opening the hub marks notifications read. On failure the next visit retries. */
    private suspend fun markUnreadAsRead() {
        val unreadIds = notificationRepository.notificationsFlow.value
            .filterNot { it.isRead }
            .map { it.id }
        if (unreadIds.isEmpty()) return

        unreadThisVisit.update { it + unreadIds }
        notificationRepository.markAsRead(unreadIds)
    }
}

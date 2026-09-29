package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.HubNotification
import com.cornellappdev.android.eatery.data.models.NotificationIds
import com.cornellappdev.android.eatery.data.models.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backs the notification hub. The server is the source of truth: read/deleted state is
 * persisted there, so local state is only updated once a request succeeds.
 */
@Singleton
class NotificationRepository @Inject constructor(
    private val networkApi: NetworkApi,
) {
    private val _notificationsFlow: MutableStateFlow<List<HubNotification>> =
        MutableStateFlow(emptyList())

    /**
     * The user's notifications from the last 24 hours, most recent first.
     */
    val notificationsFlow: StateFlow<List<HubNotification>> = _notificationsFlow.asStateFlow()

    suspend fun fetchNotifications(): Result<Unit> = resultOfNetworkCall {
        _notificationsFlow.value = networkApi.getNotifications().notifications
    }

    suspend fun markAsRead(ids: List<Int>): Result<Unit> = resultOfNetworkCall {
        networkApi.markNotificationsRead(NotificationIds(ids))
        _notificationsFlow.update { notifications ->
            notifications.map { if (it.id in ids) it.copy(isRead = true) else it }
        }
    }
}

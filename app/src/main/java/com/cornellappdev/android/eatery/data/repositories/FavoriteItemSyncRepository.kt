package com.cornellappdev.android.eatery.data.repositories

import android.util.Log
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.FavoriteItem
import com.cornellappdev.android.eatery.data.models.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mirrors local favorite items to the backend so it can send favorite-food notifications.
 * Changes are queued in DataStore and retried on the next launch if a request fails.
 */
@Singleton
class FavoriteItemSyncRepository @Inject constructor(
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authTokenRepository: AuthTokenRepository,
) {
    companion object {
        private const val LOG_TAG = "FavoriteItemSync"
    }

    // A DataStore read failure skips this sync instead of crashing; pending updates stay queued.
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.w(LOG_TAG, "Favorite item sync failed; will retry on next launch", throwable)
        }
    )
    private val itemMutexes = ConcurrentHashMap<String, Mutex>()

    fun setFavorite(itemName: String, isFavorite: Boolean) {
        scope.launch {
            userPreferencesRepository.setPendingFavoriteItemUpdate(itemName, isFavorite)
            sync(itemName)
        }
    }

    /**
     * Called once auth tokens are configured on launch: queues favorites saved before backend
     * sync existed, then retries every pending update.
     */
    fun syncPendingFavorites() {
        scope.launch {
            migrateLocalFavoritesIfNeeded()
            userPreferencesRepository.pendingFavoriteItemUpdatesFlow.first().keys.forEach { itemName ->
                launch { sync(itemName) }
            }
        }
    }

    private suspend fun migrateLocalFavoritesIfNeeded() {
        // On a read failure, skip rather than risk re-queueing every favorite on each launch.
        if (userPreferencesRepository.favoriteItemsMigratedToBackendFlow.firstOrOnReadFailure(true)) {
            return
        }

        userPreferencesRepository.favoriteItemNamesFlow.first().forEach { itemName ->
            userPreferencesRepository.setPendingFavoriteItemUpdate(itemName, true)
        }
        userPreferencesRepository.setFavoriteItemsMigratedToBackend(true)
    }

    /**
     * Sends the item's pending state to the backend. Calls for the same item run one at a time, so
     * a toggle made mid-request is sent right after it, and a duplicate call finds nothing pending.
     */
    private suspend fun sync(itemName: String) {
        authTokenRepository.awaitTokensConfigured()

        itemMutexes.getOrPut(itemName) { Mutex() }.withLock {
            val desiredState = userPreferencesRepository.pendingFavoriteItemUpdatesFlow
                .first()[itemName] ?: return

            val result = resultOfNetworkCall {
                if (desiredState) {
                    networkApi.addFavoriteItem(FavoriteItem(itemName))
                } else {
                    networkApi.deleteFavoriteItem(FavoriteItem(itemName))
                }
            }
            if (result is Result.Error) {
                Log.w(LOG_TAG, "Failed to sync favorite item; will retry: ${result.error}")
                return
            }

            // Only clears if the item wasn't toggled again while the request was in flight.
            userPreferencesRepository.clearPendingFavoriteItemUpdate(itemName, desiredState)
        }
    }
}

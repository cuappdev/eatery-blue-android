package com.cornellappdev.android.eatery.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.android.eatery.UserPreferences
import com.cornellappdev.android.eatery.util.Constants.PASSWORD_ALIAS
import com.cornellappdev.android.eatery.util.encryptData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Add flow for favorites map. Wherever filtering by favorites are needed, read from this
//  flow, and combine to filter the eateries out with the latest favorites.
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    private val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data

    /**
     * A flow automatically emitting maps indicating whether particular Eateries are favorited.
     */
    val favoritesFlow: StateFlow<Map<Int, Boolean>> = userPreferencesFlow.map { prefs ->
        prefs.favoritesMap
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, mapOf())

    val favoriteItemsFlow: StateFlow<Map<String, Boolean>> =
        userPreferencesFlow.map { prefs ->
            prefs.itemFavoritesMap
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, mapOf())

    val recentSearchesFlow: StateFlow<List<Int>> = userPreferencesFlow.map { prefs ->
        prefs.recentSearchesList
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, listOf())

    suspend fun setHasOnboarded(hasOnboarded: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setHasOnboarded(hasOnboarded).build()
        }
    }

    suspend fun setNotificationFlowCompleted(value: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setNotificationFlowCompleted(value).build()
        }
    }

    /**
     * Asynchronously sets the indicated eatery id as favorite or not.
     */
    fun setFavorite(eateryId: Int, isFavorite: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            userPreferencesStore.updateData { currentPreferences ->
                // There's no set data structure for protobuffs, so if the ID isn't in the map then
                // it isn't a favorite (hence the removal instead of making false)
                if (isFavorite) {
                    currentPreferences.toBuilder().putFavorites(eateryId, true).build()
                } else {
                    currentPreferences.toBuilder().removeFavorites(eateryId).build()
                }
            }
        }
    }

    suspend fun toggleFavoriteMenuItem(menuItem: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val isFavorite = currentPreferences.itemFavoritesMap[menuItem] == true
            if (!isFavorite) {
                currentPreferences.toBuilder().putItemFavorites(menuItem, true).build()
            } else {
                currentPreferences.toBuilder().removeItemFavorites(menuItem).build()
            }
        }
    }

    suspend fun saveLoginInfo(username: String, password: String) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setUsername(username)
                .setPassword(encryptData(PASSWORD_ALIAS, password))
                .build()
        }
    }

    suspend fun setIsLoggedIn(isLoggdIn: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setIsLoggedIn(isLoggdIn)
                .build()
        }
    }

    suspend fun setAnalyticsDisabled(analyticsDisabled: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setAnalyticsDisabled(analyticsDisabled)
                .build()
        }
    }

    suspend fun addRecentSearch(eateryId: Int) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .addRecentSearches(eateryId)
                .build()
        }
    }

    suspend fun getHasOnboarded(): Boolean =
        userPreferencesFlow.first().hasOnboarded

    suspend fun getNotificationFlowCompleted(): Boolean =
        userPreferencesFlow.first().notificationFlowCompleted

    suspend fun getIsLoggedIn(): Boolean =
        userPreferencesFlow.first().isLoggedIn

    suspend fun getAnalyticsDisabled(): Boolean =
        userPreferencesFlow.first().analyticsDisabled

    suspend fun fetchLoginInfo(): Pair<String, String> =
        Pair(userPreferencesFlow.first().username, userPreferencesFlow.first().password)
}

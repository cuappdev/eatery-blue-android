package com.cornellappdev.android.eateryblue.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.android.eateryblue.UserPreferences
import com.cornellappdev.android.eateryblue.util.Constants.PASSWORD_ALIAS
import com.cornellappdev.android.eateryblue.util.encryptData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Add flow for favorites map. Wherever filtering by favorites are needed, read from this
//  flow, and combine to filter the eateries out with the latest favorites.
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    private val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data

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

    suspend fun setFavorite(eateryId: Int, isFavorite: Boolean) {
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
        val recentSearches = userPreferencesFlow.first().recentSearchesList
        recentSearches.add(0, eateryId)
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearRecentSearches()
                .addAllRecentSearches(recentSearches.take(10))
                .build()
        }
    }

    suspend fun getFavoritesMap(): Map<Int, Boolean> =
        userPreferencesFlow.first().favoritesMap

    suspend fun getFavorite(eateryId: Int): Boolean =
        userPreferencesFlow.first().getFavoritesOrDefault(eateryId, false)

    suspend fun getHasOnboarded(): Boolean =
        userPreferencesFlow.first().hasOnboarded

    suspend fun getNotificationFlowCompleted(): Boolean =
        userPreferencesFlow.first().notificationFlowCompleted

    suspend fun getIsLoggedIn(): Boolean =
        userPreferencesFlow.first().isLoggedIn

    suspend fun getRecentSearches(): List<Int> =
        userPreferencesFlow.first().recentSearchesList

    suspend fun getAnalyticsDisabled(): Boolean =
        userPreferencesFlow.first().analyticsDisabled

    suspend fun fetchLoginInfo(): Pair<String, String> =
        Pair(userPreferencesFlow.first().username, userPreferencesFlow.first().password)
}

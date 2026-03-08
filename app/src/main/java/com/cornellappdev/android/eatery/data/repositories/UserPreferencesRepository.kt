package com.cornellappdev.android.eatery.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.android.eatery.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    companion object {
        private const val MAX_RECENT_SEARCHES = 20
    }

    private val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data

    val hasOnboardedFlow: Flow<Boolean> = userPreferencesFlow.map { it.hasOnboarded }
    val notificationFlowCompletedFlow: Flow<Boolean> =
        userPreferencesFlow.map { it.notificationFlowCompleted }
    val analyticsDisabledFlow: Flow<Boolean> = userPreferencesFlow.map { it.analyticsDisabled }
    val accessTokenFlow: Flow<String?> = userPreferencesFlow.map { it.accessToken.nullIfEmpty() }
    val refreshTokenFlow: Flow<String?> = userPreferencesFlow.map { it.refreshToken.nullIfEmpty() }
    val isLoggedInFlow: Flow<Boolean> = userPreferencesFlow.map { it.isLoggedIn }
    val pinFlow: Flow<Int> = userPreferencesFlow.map { it.pin }
    val sessionIdFlow: Flow<String> = userPreferencesFlow.map { it.sessionId }
    val favoriteEateryNamesFlow: Flow<List<String>> =
        userPreferencesFlow.map { it.favoriteEateryNamesList }
    val favoriteItemNamesFlow: Flow<List<String>> =
        userPreferencesFlow.map { it.itemFavoritesMap.keys.toList() }

    val recentSearchesFlow: Flow<List<Int>> = userPreferencesFlow.map { it.recentSearchesList }

    suspend fun setHasOnboarded(hasOnboarded: Boolean) = setPref {
        setHasOnboarded(hasOnboarded)
    }

    suspend fun setNotificationFlowCompleted(value: Boolean) = setPref {
        setNotificationFlowCompleted(value)
    }

    suspend fun setAnalyticsDisabled(analyticsDisabled: Boolean) = setPref {
        setAnalyticsDisabled(analyticsDisabled)
    }

    suspend fun addRecentSearch(eateryId: Int) = setPref {
        val updatedRecentSearches = recentSearchesList
            .filter { it != eateryId }
            .toMutableList()
            .apply { add(eateryId) }
            .takeLast(MAX_RECENT_SEARCHES)
        clearRecentSearches()
        addAllRecentSearches(updatedRecentSearches)
    }

    suspend fun setFavoriteEateryName(eateryName: String, isFavorite: Boolean) {
        setPref {
            val updatedFavorites = favoriteEateryNamesList
                .filter { it != eateryName }
                .toMutableList()

            if (isFavorite) {
                updatedFavorites.add(eateryName)
            }

            clearFavoriteEateryNames()
            addAllFavoriteEateryNames(updatedFavorites)
        }
    }

    suspend fun setFavoriteItemName(itemName: String, isFavorite: Boolean) {
        setPref {
            if (isFavorite) {
                putItemFavorites(itemName, true)
            } else {
                removeItemFavorites(itemName)
            }
        }
    }

    private suspend fun setPref(setter: UserPreferences.Builder.() -> UserPreferences.Builder) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setter()
                .build()
        }
    }

    // This approach avoids race conditions by performing get and set inside
    // updateData which is atomic
    suspend fun getOrCreateDeviceId(): String {
        var resolvedDeviceId: String? = null
        userPreferencesStore.updateData { currentPreferences ->
            val existingDeviceId = currentPreferences.deviceId.nullIfEmpty()
            if (existingDeviceId != null) {
                resolvedDeviceId = existingDeviceId
                currentPreferences
            } else {
                val newDeviceId = UUID.randomUUID().toString()
                resolvedDeviceId = newDeviceId
                currentPreferences.toBuilder()
                    .setDeviceId(newDeviceId)
                    .build()
            }
        }
        return checkNotNull(resolvedDeviceId)
    }

    private fun String?.nullIfEmpty(): String? = if (this.isNullOrEmpty()) null else this

    suspend fun setAccessToken(accessToken: String) {
        setPref { setAccessToken(accessToken) }
    }

    suspend fun setRefreshToken(refreshToken: String) {
        setPref { setRefreshToken(refreshToken) }
    }

    suspend fun setIsLoggedIn(loggedIn: Boolean) = setPref {
        setIsLoggedIn(loggedIn)
    }

    suspend fun setPin(pin: Int) {
        setPref { setPin(pin) }
    }

    suspend fun setSessionId(sessionId: String) {
        setPref { setSessionId(sessionId) }
    }
}

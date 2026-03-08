package com.cornellappdev.android.eatery.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.android.eatery.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    private val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data

    val recentSearchesFlow: StateFlow<List<Int>> = userPreferencesFlow.map { prefs ->
        prefs.recentSearchesList
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, listOf())

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
        addRecentSearches(eateryId)
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

    suspend fun getHasOnboarded(): Boolean =
        userPreferencesFlow.first().hasOnboarded

    suspend fun getNotificationFlowCompleted(): Boolean =
        userPreferencesFlow.first().notificationFlowCompleted

    suspend fun getAnalyticsDisabled(): Boolean =
        userPreferencesFlow.first().analyticsDisabled

    private suspend fun setPref(setter: UserPreferences.Builder.() -> UserPreferences.Builder) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setter()
                .build()
        }
    }

    suspend fun setDeviceId(deviceId: java.util.UUID) {
        setPref { setDeviceId(deviceId.toString()) }
    }

    private fun getStringPref(s: String?): String? {
        return if (s.isNullOrEmpty()) null else s
    }

    suspend fun getDeviceId(): String? {
        return getStringPref(userPreferencesFlow.firstOrNull()?.deviceId)
    }

    suspend fun getAccessToken(): String? {
        return getStringPref(userPreferencesFlow.firstOrNull()?.accessToken)
    }

    suspend fun setAccessToken(accessToken: String) {
        setPref { setAccessToken(accessToken) }
    }

    suspend fun getRefreshToken(): String? {
        return getStringPref(userPreferencesFlow.firstOrNull()?.refreshToken)
    }

    suspend fun setRefreshToken(refreshToken: String) {
        setPref { setRefreshToken(refreshToken) }
    }

    suspend fun getIsLoggedIn(): Boolean {
        val flow = userPreferencesFlow.firstOrNull()
        return flow?.isLoggedIn ?: false
    }

    suspend fun setIsLoggedIn(loggedIn: Boolean) = setPref {
        setIsLoggedIn(loggedIn)
    }

    suspend fun getPin(): Int = userPreferencesFlow.first().pin

    suspend fun setPin(pin: Int) {
        setPref { setPin(pin) }
    }

    suspend fun getSessionId(): String = userPreferencesFlow.first().sessionId
    suspend fun setSessionId(sessionId: String) {
        setPref { setSessionId(sessionId) }
    }

    suspend fun getFavoriteEateryNames(): List<String> =
        userPreferencesFlow.firstOrNull()?.favoriteEateryNamesList ?: emptyList()

    suspend fun getFavoriteItemNames(): List<String> =
        userPreferencesFlow.firstOrNull()?.itemFavoritesMap?.keys?.toList() ?: emptyList()
}

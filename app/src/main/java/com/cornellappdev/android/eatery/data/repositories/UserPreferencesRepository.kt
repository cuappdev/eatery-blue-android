package com.cornellappdev.android.eatery.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.android.eatery.UserPreferences
import com.cornellappdev.android.eatery.util.decryptData
import com.cornellappdev.android.eatery.util.encryptData
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

        // Android KeyStore key aliases for sensitive credential fields
        private const val ALIAS_ACCESS_TOKEN = "eatery_access_token"
        private const val ALIAS_REFRESH_TOKEN = "eatery_refresh_token"
        private const val ALIAS_SESSION_ID = "eatery_session_id"
        private const val ALIAS_DEVICE_ID = "eatery_device_id"
        private const val ALIAS_PIN = "eatery_pin"
    }

    private val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data

    val hasOnboardedFlow: Flow<Boolean> = userPreferencesFlow.map { it.hasOnboarded }
    val notificationFlowCompletedFlow: Flow<Boolean> =
        userPreferencesFlow.map { it.notificationFlowCompleted }
    val analyticsDisabledFlow: Flow<Boolean> = userPreferencesFlow.map { it.analyticsDisabled }

    /**
     * Emits the decrypted access token, or null if absent or decryption fails.
     * Tokens are encrypted at rest using AES/GCM via the Android KeyStore.
     */
    val accessTokenFlow: Flow<String?> = userPreferencesFlow.map { prefs ->
        decryptOrNull(ALIAS_ACCESS_TOKEN, prefs.accessToken)
    }

    /**
     * Emits the decrypted refresh token, or null if absent or decryption fails.
     */
    val refreshTokenFlow: Flow<String?> = userPreferencesFlow.map { prefs ->
        decryptOrNull(ALIAS_REFRESH_TOKEN, prefs.refreshToken)
    }

    val isLoggedInFlow: Flow<Boolean> = userPreferencesFlow.map { it.isLoggedIn }

    /**
     * Emits the decrypted PIN. Prefers the encrypted [UserPreferences.encryptedPin] field;
     * falls back to the legacy plaintext [UserPreferences.pin] field for migration or on
     * decryption failure.
     */
    val pinFlow: Flow<Int> = userPreferencesFlow.map { prefs ->
        decryptOrDefault(ALIAS_PIN, prefs.encryptedPin) { prefs.pin.toString() }
            .toIntOrNull() ?: prefs.pin
    }

    /**
     * Emits the decrypted session ID, or an empty string if absent or decryption fails.
     */
    val sessionIdFlow: Flow<String> = userPreferencesFlow.map { prefs ->
        decryptOrDefault(ALIAS_SESSION_ID, prefs.sessionId) { "" }
    }

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

    // The device ID is encrypted using the Android KeyStore. Legacy unencrypted UUIDs
    // (stored by a previous app version) are re-encrypted transparently on first access.
    suspend fun getOrCreateDeviceId(): String {
        var resolvedDeviceId: String? = null
        userPreferencesStore.updateData { currentPreferences ->
            val existingRaw = currentPreferences.deviceId.nullIfEmpty()
            if (existingRaw != null) {
                val decrypted = decryptOrNull(ALIAS_DEVICE_ID, existingRaw)
                if (decrypted != null) {
                    resolvedDeviceId = decrypted
                    currentPreferences
                } else {
                    // Legacy plaintext UUID - re-encrypt it and update the store.
                    resolvedDeviceId = existingRaw
                    currentPreferences.toBuilder()
                        .setDeviceId(encryptOrEmpty(ALIAS_DEVICE_ID, existingRaw))
                        .build()
                }
            } else {
                val newDeviceId = UUID.randomUUID().toString()
                resolvedDeviceId = newDeviceId
                currentPreferences.toBuilder()
                    .setDeviceId(encryptOrEmpty(ALIAS_DEVICE_ID, newDeviceId))
                    .build()
            }
        }
        return checkNotNull(resolvedDeviceId)
    }


    /** Encrypts [accessToken] before persisting. Pass an empty string to clear the value. */
    suspend fun setAccessToken(accessToken: String) {
        setPref { setAccessToken(encryptOrEmpty(ALIAS_ACCESS_TOKEN, accessToken)) }
    }

    /** Encrypts [refreshToken] before persisting. Pass an empty string to clear the value. */
    suspend fun setRefreshToken(refreshToken: String) {
        setPref { setRefreshToken(encryptOrEmpty(ALIAS_REFRESH_TOKEN, refreshToken)) }
    }

    suspend fun setIsLoggedIn(loggedIn: Boolean) = setPref {
        setIsLoggedIn(loggedIn)
    }

    /**
     * Encrypts [pin] and stores it in the [UserPreferences.encryptedPin] field.
     * Clears the legacy plaintext [UserPreferences.pin] field at the same time.
     */
    suspend fun setPin(pin: Int) {
        val toStore = encryptOrEmpty(ALIAS_PIN, pin.toString())
        setPref { setEncryptedPin(toStore).setPin(0) }
    }

    /** Encrypts [sessionId] before persisting. Pass an empty string to clear the value. */
    suspend fun setSessionId(sessionId: String) {
        setPref { setSessionId(encryptOrEmpty(ALIAS_SESSION_ID, sessionId)) }
    }

    private fun String?.nullIfEmpty(): String? = if (this.isNullOrEmpty()) null else this

    private fun decryptOrNull(alias: String, encryptedValue: String?): String? {
        val stored = encryptedValue.nullIfEmpty() ?: return null
        return runCatching { decryptData(alias, stored) }.getOrNull()
    }

    private fun decryptOrDefault(
        alias: String,
        encryptedValue: String?,
        defaultValue: () -> String,
    ): String {
        val stored = encryptedValue.nullIfEmpty() ?: return defaultValue()
        return runCatching { decryptData(alias, stored) }.getOrElse { defaultValue() }
    }

    private fun encryptOrEmpty(alias: String, rawValue: String): String {
        return if (rawValue.isEmpty()) "" else encryptData(alias, rawValue)
    }
}

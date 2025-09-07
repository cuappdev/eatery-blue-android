package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.api.FavoriteItemsApi
import com.cornellappdev.android.eatery.data.models.Cache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache backed repository for storing favorite items.
 */
@Singleton
class FavoriteItemsRepository @Inject constructor(
    private val favoriteItemsApi: FavoriteItemsApi,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    val favoriteItemsCache = Cache<String, Boolean>()

    /**
     * Sets whether a menu item is favorite.
     */
    suspend fun setFavoriteMenuItem(menuItem: String, isFavorite: Boolean) {
        favoriteItemsCache.put(menuItem, isFavorite)
        try {
            if (isFavorite) {
                favoriteItemsApi.favoriteItem(menuItem)
            } else {
                favoriteItemsApi.removeFavoriteItem(menuItem)
            }
            userPreferencesRepository.setFavoriteMenuItem(menuItem, isFavorite)
        } catch (_: Exception) {
            favoriteItemsCache.remove(menuItem)
        }
    }

    /**
     * Returns a flow containing the latest view state for whether a menu item is favorite.
     */
    fun getIsFavorite(menuItem: String): Flow<Boolean> =
        combine(
            favoriteItemsCache.get(menuItem),
            userPreferencesRepository.favoriteItemsFlow
        ) { cachedFavoriteState, actualFavoriteState ->
            cachedFavoriteState ?: actualFavoriteState.getOrDefault(menuItem, false)
        }
}
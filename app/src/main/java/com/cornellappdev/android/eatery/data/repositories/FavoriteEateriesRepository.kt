package com.cornellappdev.android.eatery.data.repositories

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteEateriesRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    val favoritesFlow: StateFlow<Map<Int, Boolean>> = TODO()

    fun setFavorite(eateryId: Int, isFavorite: Boolean) {
        TODO()
    }
}
package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * View Model for Nearest to You screen.
 */
@HiltViewModel
class NearestViewModel @Inject constructor(
    private val eateryRepository: EateryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    /**
     * A flow emitting all the eateries the user has favorited.
     */
    val favoriteEateries =
        combine(
            eateryRepository.homeEateryFlow,
            userPreferencesRepository.favoritesFlow
        ) { apiResponse, favorites ->
            when (apiResponse) {
                is EateryApiResponse.Error -> listOf()
                is EateryApiResponse.Pending -> listOf()
                is EateryApiResponse.Success -> {
                    apiResponse.data.filter {
                        favorites[it.id] == true
                    }
                        .sortedBy { it.name }
                        .sortedBy { it.isClosed() }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    /**
     * A [StateFlow] that emits all eateries sorted based off distance.
     *
     * Sorted (by descending priority): Open/Closed, Walk Time
     */
    val nearestEateries: StateFlow<List<Eatery>> =
        eateryRepository.homeEateryFlow.map { apiResponse ->
            when (apiResponse) {
                is EateryApiResponse.Error -> listOf()
                is EateryApiResponse.Pending -> listOf()
                is EateryApiResponse.Success -> {
                    apiResponse.data.sortedBy { it.getWalkTimes() }.sortedBy { it.isClosed() }

                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    /**
     * Changes the favorite status of the given eatery.
     */
    fun setFavorite(eateryId: Int?, favorite: Boolean) {
        if (eateryId != null) userPreferencesRepository.setFavorite(eateryId, favorite)
    }
}

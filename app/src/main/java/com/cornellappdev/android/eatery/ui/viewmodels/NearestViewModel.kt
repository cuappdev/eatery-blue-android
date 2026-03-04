package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View Model for Nearest to You screen.
 */
@HiltViewModel
class NearestViewModel @Inject constructor(
    eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    /**
     * A flow emitting all the eateries the user has favorited.
     */
    val favoriteEateries =
        combine(
            eateryRepository.eateryFlow,
            userRepository.favoriteEateriesFlow
        ) { apiResponse, favoriteEateries ->
            when (apiResponse) {
                is EateryApiResponse.Error -> listOf()
                is EateryApiResponse.Pending -> listOf()
                is EateryApiResponse.Success -> {
                    apiResponse.data.filter { it.name in favoriteEateries }
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
        eateryRepository.eateryFlow.map { apiResponse ->
            when (apiResponse) {
                is EateryApiResponse.Error -> listOf()
                is EateryApiResponse.Pending -> listOf()
                is EateryApiResponse.Success -> {
                    apiResponse.data.sortedBy { it.getWalkTimeInMinutes() }
                        .sortedBy { it.isClosed() }

                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    /**
     * Changes the favorite status of the given eatery.
     */
    fun setFavorite(eateryId: Int, eateryName: String, favorite: Boolean) {
        viewModelScope.launch {
            if (favorite) {
                userRepository.addFavoriteEatery(eateryId, eateryName)
            } else {
                userRepository.removeFavoriteEatery(eateryId, eateryName)
            }
        }
    }
}

package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.repositories.EateryRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * View Model for Nearest to You screen.
 */
@HiltViewModel
class NearestViewModel@Inject constructor(
    private val eateryRepository: EateryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    /**
     * A [StateFlow] that emits all eateries sorted based off distance.
     *
     * Sorted (by descending priority): Open/Closed, Walk Time
     */
    val nearestEateries: StateFlow<List<Eatery>> = eateryRepository.homeEateryFlow.map { apiResponse ->
        when (apiResponse) {
            is EateryApiResponse.Error -> listOf()
            is EateryApiResponse.Pending -> listOf()
            is EateryApiResponse.Success -> {
                apiResponse.data.sortedBy { it.getWalkTimes() }.sortedBy { it.isClosed() }

            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun removeFavorite(eateryId: Int?) {
        if (eateryId != null) userPreferencesRepository.setFavorite(eateryId, false)
    }
}

package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    data class NearestUiState(
        val nearestEateries: List<Eatery> = emptyList(),
        val favoriteEateries: List<Eatery> = emptyList(),
        val error: NetworkUiError? = null
    )

    private val _error = MutableStateFlow<NetworkUiError?>(null)

    fun clearError() {
        _error.value = null
    }

    /**
     * A flow emitting all the eateries the user has favorited.
     */
    private val favoriteEateries =
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
    private val nearestEateries: StateFlow<List<Eatery>> =
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

    val uiState: StateFlow<NearestUiState> = combine(
        nearestEateries,
        favoriteEateries,
        _error
    ) { nearest, favorites, networkError ->
        NearestUiState(
            nearestEateries = nearest,
            favoriteEateries = favorites,
            error = networkError
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NearestUiState())

    /**
     * Changes the favorite status of the given eatery.
     */
    fun setFavorite(eateryId: Int, eateryName: String, favorite: Boolean) {
        viewModelScope.launch {
            val result = if (favorite) {
                userRepository.addFavoriteEatery(eateryId, eateryName)
            } else {
                userRepository.removeFavoriteEatery(eateryId, eateryName)
            }

            when (result) {
                is Result.Success -> {
                    _error.value = null
                }

                is Result.Error -> {
                    _error.value = NetworkUiError.Failed(
                        if (favorite) NetworkAction.AddFavoriteEatery else NetworkAction.RemoveFavoriteEatery,
                        result.error
                    )
                }
            }
        }
    }
}

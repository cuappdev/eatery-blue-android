package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterData
import com.cornellappdev.android.eatery.ui.components.general.updateFilters
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _filtersFlow: MutableStateFlow<List<Filter>> = MutableStateFlow(listOf())

    private val _error = MutableStateFlow<NetworkUiError?>(null)
    val error = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    /**
     * A flow of filters applied to the screen.
     */
    val filtersFlow = _filtersFlow.asStateFlow()

    val homeScreenFilters = listOf(
        Filter.FromEateryFilter.North,
        Filter.FromEateryFilter.West,
        Filter.FromEateryFilter.Central,
        Filter.FromEateryFilter.Swipes,
        Filter.FromEateryFilter.BRB,
        Filter.RequiresFavoriteEateries.Favorites,
        Filter.FromEateryFilter.Under10,
    )

    /**
     * A flow emitting all eateries with the appropriate filters applied.
     *
     * Sorted (by descending priority): Open/Closed, Alphabetically
     */
    val eateryFlow: StateFlow<EateryApiResponse<List<Eatery>>> =
        combine(
            eateryRepository.eateryFlow,
            _filtersFlow,
            userRepository.favoriteEateriesFlow
        ) { apiResponse, filters, favoriteEateries ->
            when (apiResponse) {
                is EateryApiResponse.Error -> EateryApiResponse.Error
                is EateryApiResponse.Pending -> EateryApiResponse.Pending
                is EateryApiResponse.Success -> {
                    val eateries = apiResponse.data
                    val favoriteEateryIds =
                        eateries.filter { it.id != null }
                            .associate { it.id!! to (it.name in favoriteEateries) }
                    EateryApiResponse.Success(
                        apiResponse.data.filter { eatery ->
                            Filter.passesSelectedFilters(
                                allFilters = homeScreenFilters,
                                selectedFilters = filters,
                                filterData = FilterData(
                                    eatery,
                                    favoriteEateryIds = favoriteEateryIds
                                )
                            )
                        }.sortedBy { eatery ->
                            eatery.name
                        }.sortedBy { eatery ->
                            eatery.isClosed()
                        })
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, EateryApiResponse.Pending)


    /**
     * A flow emitting all the eateries the user has favorited.
     */
    val favoriteEateries =
        combine(
            eateryRepository.eateryFlow,
            userRepository.favoriteEateriesFlow
        ) { apiResponse, favorites ->
            when (apiResponse) {
                is EateryApiResponse.Error -> listOf()
                is EateryApiResponse.Pending -> listOf()
                is EateryApiResponse.Success -> {
                    apiResponse.data.filter {
                        it.name in favorites
                    }
                        .sortedBy { it.name }
                        .sortedBy { it.isClosed() }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    /**A [StateFlow] that emits a list of all eateries sorted by nearest proximity
     *
     * Sorted (by descending priority): Open/Closed, Walk Time
     *
     * TODO: (from old nearestEateries function) Walk times may not be updating automatically; may have to change location to use state.
     * */
    val eateriesByDistance: StateFlow<List<Eatery>> = eateryFlow.map { apiResponse ->
        when (apiResponse) {
            is EateryApiResponse.Error -> listOf()
            is EateryApiResponse.Pending -> listOf()
            is EateryApiResponse.Success -> {
                apiResponse.data.sortedBy { it.getWalkTimeInMinutes() }.sortedBy { it.isClosed() }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    var bigPopUp by mutableStateOf(false)

    fun setPopUp(bool: Boolean) {
        bigPopUp = bool
    }

    fun onToggleFilterPressed(filter: Filter) {
        if (eateryFlow.value is EateryApiResponse.Error) {
            pingEateries()
        }
        _filtersFlow.update {
            it.updateFilters(filter)
        }
    }

    fun addPaymentMethodFilters(filters: List<Filter>) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.removeAll(Filter.paymentMethodFilters)
        newList.addAll(filters)
        _filtersFlow.value = newList
    }

    fun onResetFiltersClicked() {
        if (eateryFlow.value is EateryApiResponse.Error) {
            pingEateries()
        }
        _filtersFlow.update { emptyList() }
    }

    fun addFavoriteEatery(eateryId: Int, eateryName: String) {
        viewModelScope.launch {
            when (val result = userRepository.addFavoriteEatery(eateryId, eateryName)) {
                is Result.Success -> {
                    _error.value = null
                }

                is Result.Error -> {
                    _error.value =
                        NetworkUiError.Failed(NetworkAction.AddFavoriteEatery, result.error)
                }
            }
        }
    }

    fun removeFavoriteEatery(eateryId: Int, eateryName: String) {
        viewModelScope.launch {
            when (val result = userRepository.removeFavoriteEatery(eateryId, eateryName)) {
                is Result.Success -> {
                    _error.value = null
                }

                is Result.Error -> {
                    _error.value =
                        NetworkUiError.Failed(NetworkAction.RemoveFavoriteEatery, result.error)
                }
            }
        }
    }

    fun getNotificationFlowCompleted() = runBlocking {
        return@runBlocking userPreferencesRepository.getNotificationFlowCompleted()
    }

    fun setNotificationFlowCompleted(value: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setNotificationFlowCompleted(value)
    }

    fun pingEateries() {
        eateryRepository.pingEateries()
    }

    fun updateFavoritesIfTokensConfigured() {
        if (userRepository.tokensConfiguredFlow.value) {
            viewModelScope.launch {
                when (val result = userRepository.updateFavorites()) {
                    is Result.Success -> {
                        _error.value = null
                    }

                    is Result.Error -> {
                        _error.value =
                            NetworkUiError.Failed(NetworkAction.GetFavorites, result.error)
                    }
                }
            }
        }
    }
}

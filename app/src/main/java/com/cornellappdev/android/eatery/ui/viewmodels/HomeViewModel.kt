package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterData
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    private val _filtersFlow: MutableStateFlow<List<Filter>> = MutableStateFlow(listOf())

    /**
     * A flow of filters applied to the screen.
     */
    val filtersFlow = _filtersFlow.asStateFlow()

    val homeScreenFilters = listOf(
        Filter.FromEatery.North,
        Filter.FromEatery.West,
        Filter.FromEatery.Central,
        Filter.FromEatery.Swipes,
        Filter.FromEatery.BRB,
        Filter.RequiresFavoriteEateries.Favorites,
        Filter.FromEatery.Under10,
    )

    /**
     * A flow emitting all eateries with the appropriate filters applied.
     *
     * Sorted (by descending priority): Open/Closed, Alphabetically
     */
    val eateryFlow: StateFlow<EateryApiResponse<List<Eatery>>> =
        combine(
            eateryRepository.homeEateryFlow,
            _filtersFlow,
            userPreferencesRepository.favoritesFlow
        ) { apiResponse, filters, favorites ->
            when (apiResponse) {
                is EateryApiResponse.Error -> EateryApiResponse.Error
                is EateryApiResponse.Pending -> EateryApiResponse.Pending
                is EateryApiResponse.Success -> {
                    EateryApiResponse.Success(
                        apiResponse.data.filter { eatery ->
                            Filter.passesSelectedFilters(
                                allFilters = homeScreenFilters,
                                selectedFilters = filters,
                                filterData = FilterData(
                                    eatery,
                                    favoriteEateryIds = favorites
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
                apiResponse.data.sortedBy { it.getWalkTimes() }.sortedBy { it.isClosed() }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    var bigPopUp by mutableStateOf(false)

    fun setPopUp(bool: Boolean) {
        bigPopUp = bool
    }

    fun addFilter(filter: Filter) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.add(filter)
        _filtersFlow.value = newList
    }

    fun removeFilter(filter: Filter) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.remove(filter)
        _filtersFlow.value = newList
    }

    fun addPaymentMethodFilters(filters: List<Filter>) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.removeAll(Filter.paymentMethodFilters)
        newList.addAll(filters)
        _filtersFlow.value = newList
    }

    fun resetFilters() = viewModelScope.launch {
        _filtersFlow.value = listOf()
    }

    /**
     * Determines if the eatery passes the filter inspection based on what's currently selected.
     */


    fun addFavorite(eateryId: Int?) {
        if (eateryId != null)
            userPreferencesRepository.setFavorite(eateryId, true)
    }

    fun removeFavorite(eateryId: Int?) {
        if (eateryId != null)
            userPreferencesRepository.setFavorite(eateryId, false)
    }

    fun getNotificationFlowCompleted() = runBlocking {
        return@runBlocking userPreferencesRepository.getNotificationFlowCompleted()
    }

    fun setNotificationFlowCompleted(value: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setNotificationFlowCompleted(value)
    }
}

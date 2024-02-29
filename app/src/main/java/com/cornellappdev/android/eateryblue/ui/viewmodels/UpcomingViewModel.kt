package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.repositories.EateryRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.ui.components.general.Filter
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    private val _filtersFlow: MutableStateFlow<List<Filter>> =
        MutableStateFlow(listOf(Filter.BREAKFAST))

    /**
     * A flow of filters applied to the screen.
     */
    val filtersFlow = _filtersFlow.asStateFlow()

    /**
     * A flow emitting all eateries with the appropriate filters applied.
     */
    val eateryFlow = eateryRepository.eateryFlow.combine(_filtersFlow) { apiResponse, filters ->
        when (apiResponse) {
            is EateryApiResponse.Error -> EateryApiResponse.Error
            is EateryApiResponse.Pending -> EateryApiResponse.Pending
            is EateryApiResponse.Success -> {
                EateryApiResponse.Success(
                    apiResponse.data.filter {
                        passesFilter(it, filters)
                    })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EateryApiResponse.Pending)

    fun addFilter(filter: Filter) = viewModelScope.launch {
        addLocationFilters(filter)
    }

    fun removeFilter(filter: Filter) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.remove(filter)
        _filtersFlow.value = newList
    }

    fun addMealFilters(filters: List<Filter>) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.removeAll(Filter.MEALS)
        newList.addAll(filters)
        _filtersFlow.value = newList
    }

    fun resetFilters() = viewModelScope.launch {
        _filtersFlow.value = listOf()
    }

    private fun addLocationFilters(filter: Filter) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.add(filter)
        _filtersFlow.value = newList
    }

    private fun passesFilter(eatery: Eatery, filters: List<Filter>): Boolean {
        val allDiningHalls = eatery.paymentAcceptsMealSwipes ?: false

        val allLocationsValid =
            !filters.contains(Filter.NORTH) &&
                    !filters.contains(Filter.CENTRAL) &&
                    !filters.contains(Filter.WEST)

        // Passes filter if all locations aren't selected (therefore any location is valid, specified by allLocationsValid)
        // or one/multiple are selected and the eatery is located there.
        return allDiningHalls
                &&
                (allLocationsValid || ((filters.contains(Filter.NORTH) && eatery.campusArea == "North") ||
                        (filters.contains(Filter.WEST) && eatery.campusArea == "West") ||
                        (filters.contains(Filter.CENTRAL) && eatery.campusArea == "Central")))
    }

}

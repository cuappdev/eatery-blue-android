package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.repositories.EateryRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.ui.components.general.Filter
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    var eateryRetrievalState: EateryRetrievalState by mutableStateOf(EateryRetrievalState.Pending)
        private set

    private val _currentFiltersSelected = mutableStateListOf<Filter>()
    val currentFiltersSelected: List<Filter> = _currentFiltersSelected

    private var initial: Boolean = true

    private val _allEateries = mutableSetOf<Eatery>()
    val allEateries: Set<Eatery> = _allEateries

    var filteredResults = mutableStateListOf<Eatery>()
        private set

    init {
        queryAllEateries()
    }

    // TODO: Change to directly read from [EateryRepository]'s `eateryFlow`
    private fun queryAllEateries() = viewModelScope.launch {
        eateryRetrievalState = try {
            val eateryResponse = eateryRepository.getAllEateries()
            _allEateries.addAll(eateryResponse)
            EateryRetrievalState.Success
        } catch (_: Exception) {
            EateryRetrievalState.Error
        }
    }

    private fun queryAllEvents() = viewModelScope.launch {
        try {
            val eateryResponse = eateryRepository.getAllEvents()
            if (eateryResponse.success) {
                eateryRetrievalState = EateryRetrievalState.Success
            }
        } catch (_: Exception) {
            eateryRetrievalState = EateryRetrievalState.Error
        }
    }

    fun initializeFilter() = viewModelScope.launch {
        if (initial) {
            _currentFiltersSelected.add(Filter.BREAKFAST)
        }
        initial = false
        filterEateries()


    }

    fun addFilter(filter: Filter) = viewModelScope.launch {
        addLocationFilters(filter)
        filterEateries()
    }

    fun removeFilter(filter: Filter) = viewModelScope.launch {
        _currentFiltersSelected.remove(filter)
        filterEateries()
    }

    fun addMealFilters(filters: List<Filter>) = viewModelScope.launch {
        _currentFiltersSelected.removeAll(Filter.MEALS)
        _currentFiltersSelected.addAll(filters)
        filterEateries()
    }

    fun resetFilters() = viewModelScope.launch {
        _currentFiltersSelected.clear()
    }

    private fun addLocationFilters(filter: Filter) = viewModelScope.launch {
        _currentFiltersSelected.add(filter)
        val locations = _currentFiltersSelected.filter { Filter.LOCATIONS.contains(it) }
        if (locations.size == 3) {
            _currentFiltersSelected.removeAll(Filter.LOCATIONS)
        }
        filterEateries()

    }

    private fun filterEateries() = viewModelScope.launch {
        filteredResults = _allEateries.filter { eatery ->
            passesFilter(eatery)
        }.toCollection(mutableStateListOf())
    }

    private fun passesFilter(eatery: Eatery): Boolean {
        var passesFilter: Boolean

        val allDiningHalls = eatery.paymentAcceptsMealSwipes ?: false

        val allLocationsValid =
            !_currentFiltersSelected.contains(Filter.NORTH) &&
                !_currentFiltersSelected.contains(Filter.CENTRAL) &&
                !_currentFiltersSelected.contains(Filter.WEST)

        // Passes filter if all locations aren't selected (therefore any location is valid, specified by allLocationsValid)
        // or one/multiple are selected and the eatery is located there.
        passesFilter = allDiningHalls
            &&
            (allLocationsValid || ((_currentFiltersSelected.contains(Filter.NORTH) && eatery.campusArea == "North") ||
                (_currentFiltersSelected.contains(Filter.WEST) && eatery.campusArea == "West") ||
                (_currentFiltersSelected.contains(Filter.CENTRAL) && eatery.campusArea == "Central")))

        return passesFilter
    }

}

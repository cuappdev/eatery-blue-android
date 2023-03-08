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

    private val _allEateries = mutableSetOf<Eatery>()
    val allEateries: Set<Eatery> = _allEateries

    var favoriteEateries = mutableStateListOf<Eatery>()
        private set

    var filteredResults = mutableStateListOf<Eatery>()
        private set

    init {
        queryAllEateries()
    }

    private fun queryAllEateries() = viewModelScope.launch {
        try {
            val eateryResponse = eateryRepository.getAllEateries()
            if (eateryResponse.success) {
                eateryResponse.data?.let { _allEateries.addAll(it) }
                eateryRetrievalState = EateryRetrievalState.Success
            }
        } catch (_: Exception) {
            eateryRetrievalState = EateryRetrievalState.Error
        }
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
        if (_currentFiltersSelected.contains(Filter.ALL_CAMPUS) || filter == Filter.ALL_CAMPUS) {
            _currentFiltersSelected.removeAll(Filter.LOCATIONS)
            _currentFiltersSelected.add(filter)
        } else {
            _currentFiltersSelected.add(filter)
        }
        val notLocations = _currentFiltersSelected.filterNot { !Filter.LOCATIONS.contains(it) }
        if (notLocations.size == 3) {
            _currentFiltersSelected.removeAll(Filter.LOCATIONS)
            _currentFiltersSelected.add(Filter.ALL_CAMPUS)
        }
        filterEateries()
    }

    private fun filterEateries() = viewModelScope.launch {
        filteredResults = _allEateries.filter { eatery ->
            passesFilter(eatery)
        }.toCollection(mutableStateListOf())
    }

    private fun passesFilter(eatery: Eatery): Boolean {
        var passesFilter = true
        if (_currentFiltersSelected.contains(Filter.UNDER_10)) {
            val walkTimes = eatery.getWalkTimes()
            passesFilter = walkTimes != null && walkTimes <= 10
        }

        if (_currentFiltersSelected.contains(Filter.FAVORITES)) {
            passesFilter = favoriteEateries.any {
                it.id == eatery.id
            }
        }

        val allLocationsValid =
            !_currentFiltersSelected.contains(Filter.NORTH) &&
                    !_currentFiltersSelected.contains(Filter.CENTRAL) &&
                    !_currentFiltersSelected.contains(Filter.WEST)

        // Passes filter if all locations aren't selected (therefore any location is valid, specified by allLocationsValid)
        // or one/multiple are selected and the eatery is located there.
        passesFilter = passesFilter &&
                (allLocationsValid || ((_currentFiltersSelected.contains(Filter.NORTH) && eatery.campusArea == "North") ||
                        (_currentFiltersSelected.contains(Filter.WEST) && eatery.campusArea == "West") ||
                        (_currentFiltersSelected.contains(Filter.CENTRAL) && eatery.campusArea == "Central")))

        return passesFilter
    }

}
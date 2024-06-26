package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.MealFilter
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {

    private val _mealFilterFlow: MutableStateFlow<MealFilter> =
        MutableStateFlow<MealFilter>(nextMeal() ?: MealFilter.LATE_DINNER)
    private val _locationFilterFlow: MutableStateFlow<List<Filter>> =
        MutableStateFlow(listOf())

    /**
     * A flow of filters applied to the screen.
     */

    val mealFilterFlow = _mealFilterFlow.asStateFlow()
    val locationFilterFlow = _locationFilterFlow.asStateFlow()

    /**
     * A flow emitting all eateries with the appropriate filters applied.
     */
    val eateryFlow =
        eateryRepository.eateryFlow.combine(_locationFilterFlow) { apiResponse, filters ->
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

    fun addLocationFilter(filter: Filter) = viewModelScope.launch {
        addLocationFilters(filter)
    }

    fun removeLocationFilter(filter: Filter) = viewModelScope.launch {
        val newList = _locationFilterFlow.value.toMutableList()
        newList.remove(filter)
        _locationFilterFlow.value = newList
    }

    fun changeMealFilter(filter: MealFilter) = viewModelScope.launch {
        _mealFilterFlow.value = filter
    }

    fun resetLocationFilters() = viewModelScope.launch {
        _locationFilterFlow.value = listOf()
    }

    fun resetMealFilter() = viewModelScope.launch {
        _mealFilterFlow.value = nextMeal() ?: MealFilter.LATE_DINNER
    }

    private fun addLocationFilters(filter: Filter) = viewModelScope.launch {
        val newList = _locationFilterFlow.value.toMutableList()
        newList.add(filter)
        val locations = newList.filter { Filter.LOCATIONS.contains(it) }
        if (locations.size == 3) {
            newList.removeAll(Filter.LOCATIONS)
        }
        _locationFilterFlow.value = newList
    }

    /**
     * nextMeal returns the next MealFilter that ends after the current time.
     * (Ex. If it is 10:45am, the next meal is Lunch ending at 2:30pm)
     * Returns null when no meals end after the current time.
     */
    private fun nextMeal(): MealFilter? {
        return MealFilter.values()
            .find { it.endTimes >= LocalDateTime.now().hour + LocalDateTime.now().minute / 60f }
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

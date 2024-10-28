package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.MealFilter
import com.cornellappdev.android.eatery.ui.components.general.MenuCategoryViewState
import com.cornellappdev.android.eatery.ui.components.general.MenuItemViewState
import com.cornellappdev.android.eatery.ui.components.general.passesFilter
import com.cornellappdev.android.eatery.ui.components.upcoming.EateryHours
import com.cornellappdev.android.eatery.ui.components.upcoming.EateryStatus
import com.cornellappdev.android.eatery.ui.components.upcoming.MenuCardViewState
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.theme.Orange
import com.cornellappdev.android.eatery.ui.theme.Red
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class EateriesSection(
    val header: String,
    val menuCards: List<MenuCardViewState>,
)

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    eateryRepository: EateryRepository
) : ViewModel() {

    private val _mealFilterFlow = MutableStateFlow(nextMeal() ?: MealFilter.LATE_DINNER)
    private val _locationFilterFlow = MutableStateFlow(listOf<Filter>())


    /**
     * A flow emitting all eateries with the appropriate filters applied.
     */
    val viewStateFlow: StateFlow<EateryApiResponse<List<EateriesSection>>> = combine(
        eateryRepository.eateryFlow,
        _locationFilterFlow,
        userPreferencesRepository.favoriteItemsFlow,
        userPreferencesRepository.favoritesFlow,
        _mealFilterFlow
    ) { eateryApiResponse, filters, favoriteItemsMap, favoriteEateriesMap, mealFilter ->

        fun Eatery.toMenuCardViewState(): MenuCardViewState? {
            val currentEvent = events?.find { it.description in mealFilter.text } ?: return null
            return MenuCardViewState(
                eateryHours = currentEvent.startTime?.let { startTime ->
                    currentEvent.endTime?.let { endTime ->
                        EateryHours(
                            startTime = startTime.format(
                                DateTimeFormatter.ofPattern("TODO")
                            ),
                            endTime = startTime.format(
                                DateTimeFormatter.ofPattern("TODO")
                            )
                        )
                    }
                },
                eateryId = id ?: return null,
                menu = currentEvent.menu?.map { menu ->
                    MenuCategoryViewState(
                        menu.category ?: "",
                        menu.items?.map { menuItem ->
                            MenuItemViewState(
                                item = menuItem,
                                isFavorite = favoriteItemsMap[menuItem.name] == true
                            )
                        } ?: emptyList()
                    )
                } ?: emptyList(),
                name = name ?: "Unknown Eatery",
                eateryStatus = when {
                    isClosed() -> EateryStatus("Closed", Red)
                    isClosingSoon() -> EateryStatus("Closing Soon", Orange)
                    else -> EateryStatus("Open", Green)
                },
            )
        }

        when (eateryApiResponse) {
            is EateryApiResponse.Error -> EateryApiResponse.Error
            is EateryApiResponse.Pending -> EateryApiResponse.Pending
            is EateryApiResponse.Success -> {
                val data = eateryApiResponse.data.filter { eatery ->
                    filters.all {
                        it.passesFilter(
                            eatery,
                            favoriteEateriesMap,
                            emptyList()
                        )
                    }
                }

                val eateriesByLocation = data.groupBy { it.campusArea ?: "Unknown" }
                EateryApiResponse.Success(
                    eateriesByLocation.keys.map { location ->
                        EateriesSection(
                            header = location,
                            menuCards = eateriesByLocation[location]?.mapNotNull {
                                it.toMenuCardViewState()
                            } ?: emptyList()
                        )
                    }
                )
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

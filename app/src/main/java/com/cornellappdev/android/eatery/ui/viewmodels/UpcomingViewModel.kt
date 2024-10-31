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
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class UpcomingMenusViewState(
    val menus: EateryApiResponse<List<EateriesSection>> = EateryApiResponse.Pending,
    val mealFilter: MealFilter = MealFilter.LATE_DINNER,
    val selectedFilters: List<Filter> = emptyList(),
    val selectedDay: Int = 0,
)

data class EateriesSection(
    val header: String,
    val menuCards: List<MenuCardViewState>,
)

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    eateryRepository: EateryRepository
) : ViewModel() {

    private val mealFilterFlow = MutableStateFlow(nextMeal() ?: MealFilter.LATE_DINNER)
    private val selectedFiltersFlow = MutableStateFlow(listOf<Filter>())
    private val selectedDayFlow = MutableStateFlow(0)


    /**
     * A flow emitting all eateries with the appropriate filters applied.
     */
    val viewStateFlow: StateFlow<UpcomingMenusViewState> = combine(
        eateryRepository.eateryFlow,
        selectedFiltersFlow,
        userPreferencesRepository.favoriteItemsFlow,
        mealFilterFlow,
        selectedDayFlow
    ) { eateryApiResponse, filters, favoriteItemsMap, mealFilter, selectedDayOffset ->
        val viewingDate = LocalDate.now().plusDays(selectedDayOffset.toLong())

        fun Eatery.toMenuCardViewState(): MenuCardViewState? {
            val currentEvent =
                events?.find {
                    it.description in mealFilter.text &&
                            it.startTime?.toLocalDate() == viewingDate
                }
                    ?: return null
            return MenuCardViewState(
                eateryHours = currentEvent.startTime?.let { startTime ->
                    currentEvent.endTime?.let { endTime ->
                        val timePattern = "hh:mm a"
                        EateryHours(
                            startTime = startTime.format(
                                DateTimeFormatter.ofPattern(timePattern)
                            ),
                            endTime = endTime.format(
                                DateTimeFormatter.ofPattern(timePattern)
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
            is EateryApiResponse.Error -> {
                UpcomingMenusViewState(
                    menus = EateryApiResponse.Error,
                    mealFilter = mealFilter,
                    selectedFilters = filters,
                )
            }

            is EateryApiResponse.Pending -> {
                UpcomingMenusViewState(
                    menus = EateryApiResponse.Pending,
                    mealFilter,
                    filters,
                )
            }


            is EateryApiResponse.Success -> {
                val data = eateryApiResponse.data.filter { eatery ->
                    filters.all {
                        it.passesFilter(
                            eatery,
                            // On this screen we don't display favorite eateries differently
                            //  so we just pass an empty map
                            emptyMap(),
                            // We also don't select Eateries on this screen
                            emptyList()
                        )
                    }
                }

                val eateriesByLocation = data.groupBy { it.campusArea ?: "Unknown" }
                UpcomingMenusViewState(
                    menus = EateryApiResponse.Success(
                        eateriesByLocation.filter { it.value.isNotEmpty() }.keys.mapNotNull { location ->
                            val menuCards = eateriesByLocation[location]?.mapNotNull {
                                it.toMenuCardViewState()
                            }?.takeIf { it.isNotEmpty() }
                            menuCards?.let {
                                EateriesSection(
                                    header = location,
                                    menuCards = it
                                )
                            }
                        },
                    ),
                    mealFilter = mealFilter,
                    selectedFilters = filters,
                    selectedDay = selectedDayOffset,
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UpcomingMenusViewState())

    fun addLocationFilter(filter: Filter) {
        addLocationFilters(filter)
    }

    fun removeLocationFilter(filter: Filter) {
        val newList = selectedFiltersFlow.value.toMutableList()
        newList.remove(filter)
        selectedFiltersFlow.value = newList
    }

    fun changeMealFilter(filter: MealFilter) {
        mealFilterFlow.value = filter
    }

    fun resetMealFilter() {
        mealFilterFlow.value = nextMeal() ?: MealFilter.LATE_DINNER
    }

    fun selectDayOffset(offset: Int) {
        selectedDayFlow.update { offset }
    }

    private fun addLocationFilters(filter: Filter) {
        selectedFiltersFlow.update {
            val newList = (it + filter).filter { Filter.LOCATIONS.contains(it) }

            if (newList.size == 3) {
                emptyList()
            } else newList
        }
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
}

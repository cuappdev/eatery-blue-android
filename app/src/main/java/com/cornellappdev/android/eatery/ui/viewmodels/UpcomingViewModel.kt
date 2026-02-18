package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.EateryStatus
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterData
import com.cornellappdev.android.eatery.ui.components.general.MealFilter
import com.cornellappdev.android.eatery.ui.components.general.MenuCategoryViewState
import com.cornellappdev.android.eatery.ui.components.general.MenuItemViewState
import com.cornellappdev.android.eatery.ui.components.general.updateFilters
import com.cornellappdev.android.eatery.ui.components.upcoming.EateryHours
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
    private val eateryRepository: EateryRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val mealFilterFlow = MutableStateFlow(nextMeal() ?: MealFilter.LATE_DINNER)
    private val selectedFiltersFlow = MutableStateFlow(listOf<Filter>())
    private val selectedDayFlow = MutableStateFlow(0)
    val upcomingMenuFilters =
        listOf(
            Filter.FromEateryFilter.North,
            Filter.FromEateryFilter.West,
            Filter.FromEateryFilter.Central
        )


    /**
     * A flow emitting all eateries with the appropriate filters applied.
     */
    val viewStateFlow: StateFlow<UpcomingMenusViewState> = combine(
        eateryRepository.eateryFlow,
        selectedFiltersFlow,
        userRepository.favoriteItemsFlow,
        mealFilterFlow,
        selectedDayFlow
    ) { eateryApiResponse, filters, favoriteItems, mealFilter, selectedDayOffset ->
        val viewingDate = LocalDate.now().plusDays(selectedDayOffset.toLong())

        fun Eatery.toMenuCardViewState(): MenuCardViewState? {
            val currentEvent =
                events?.find {
                    it.type in mealFilter.text &&
                            it.startTimestamp?.toLocalDate() == viewingDate
                }
                    ?: return null
            return MenuCardViewState(
                eateryHours = currentEvent.startTimestamp?.let { startTime ->
                    currentEvent.endTimestamp?.let { endTime ->
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
                        menu.name ?: "",
                        menu.items?.map { menuItem ->
                            MenuItemViewState(
                                item = menuItem,
                                isFavorite = menuItem.name in favoriteItems
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
                    selectedDay = selectedDayOffset
                )
            }

            is EateryApiResponse.Pending -> {
                UpcomingMenusViewState(
                    menus = EateryApiResponse.Pending,
                    mealFilter = mealFilter,
                    selectedFilters = filters,
                    selectedDay = selectedDayOffset
                )
            }

            is EateryApiResponse.Success -> {
                val data = eateryApiResponse.data.filter { eatery ->
                    Filter.passesSelectedFilters(upcomingMenuFilters, filters, FilterData(eatery))
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
                                    header = location.lowercase()
                                        .replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() },
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
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UpcomingMenusViewState(mealFilter = nextMeal() ?: MealFilter.LATE_DINNER)
    )

    fun onToggleFilterClicked(filter: Filter) {
        if (viewStateFlow.value.menus is EateryApiResponse.Error) {
            pingEateries()
        }
        selectedFiltersFlow.update {
            it.updateFilters(filter)
        }
    }

    fun onResetFiltersClicked() {
        if (viewStateFlow.value.menus is EateryApiResponse.Error) {
            pingEateries()
        }
        mealFilterFlow.value = nextMeal() ?: MealFilter.LATE_DINNER
        selectedFiltersFlow.update { emptyList() }
    }

    fun onMealFilterChanged(filter: MealFilter) {
        if (viewStateFlow.value.menus is EateryApiResponse.Error) {
            pingEateries()
        }
        mealFilterFlow.value = filter
    }

    fun selectDayOffset(offset: Int) {
        if (viewStateFlow.value.menus is EateryApiResponse.Error) {
            pingEateries()
        }
        selectedDayFlow.update { offset }
    }

    fun pingEateries() {
        eateryRepository.pingEateries()
    }

    /**
     * nextMeal returns the next MealFilter that ends after the current time.
     * (Ex. If it is 10:45am, the next meal is Lunch ending at 2:30pm)
     * Returns null when no meals end after the current time.
     */
    private fun nextMeal(): MealFilter? {
        return MealFilter.entries
            .find { it.endTimes >= LocalDateTime.now().hour + LocalDateTime.now().minute / 60f }
    }
}

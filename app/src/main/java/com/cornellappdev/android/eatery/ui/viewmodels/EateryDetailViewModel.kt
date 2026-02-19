package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.MenuCategoryViewState
import com.cornellappdev.android.eatery.ui.components.general.MenuItemViewState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.util.fromOffsetToDayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


sealed class EateryDetailViewState {
    object Loading : EateryDetailViewState()

    data class Loaded(
        val mealToShow: MealViewState,
        val eatery: Eatery,
        val isFavorite: Boolean,
        val weekdayIndex: Int,
    ) : EateryDetailViewState() {
        val mealTypeIndex: Int = eatery.getTypeMeal(weekdayIndex.fromOffsetToDayOfWeek())
            .indexOfFirst { it.first == mealToShow.description }.coerceAtLeast(0)
    }

    data class Error(val message: String) : EateryDetailViewState()
}


data class MealViewState(
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val menu: List<MenuCategoryViewState>?,
    val description: String?,
) {
    fun toEvent() = Event(
        type = description,
        startTimestamp = startTime,
        endTimestamp = endTime,
        menu = menu?.map { it.toMenuCategory() }?.toMutableList()
    )
}


@HiltViewModel
class EateryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    init {
        eateryRepository.changeScreen(EateryRepository.Screen.DETAILS)
    }

    private val eateryId: Int = checkNotNull(savedStateHandle["eateryId"])

    private val _eateryDetailsViewState =
        MutableStateFlow<EateryDetailViewState>(EateryDetailViewState.Loading)
    val eateryDetailViewState = _eateryDetailsViewState.asStateFlow()

    /**
     * A flow emitting the loading status of the current eatery.
     */
    private val eateryFlow: Flow<EateryApiResponse<Eatery>> =
        eateryRepository.getEateryFlow(eateryId)

    private val userSelectedMeal = MutableStateFlow<Event?>(null)

    private val _searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * A flow emitting which search query the user has typed in.
     * TODO move search logic to ViewModel
     */
    val searchQueryFlow = this._searchQueryFlow.asStateFlow()

    init {
        openEatery()
    }

    /**
     * Initializes the eatery detail view model to the given eatery id. Initializes [eateryFlow] and
     * any other relevant flows originating from it.
     */
    private fun openEatery() {
        combine(
            userRepository.favoriteEateriesFlow,
            userRepository.favoriteItemsFlow,
            eateryFlow,
            userSelectedMeal
        ) { favoriteEateries, favoriteItems, eatery, userSelectedMeal ->
            when (eatery) {
                EateryApiResponse.Error -> _eateryDetailsViewState.update {
                    EateryDetailViewState.Error("TODO")
                }

                EateryApiResponse.Pending -> _eateryDetailsViewState.update {
                    EateryDetailViewState.Loading
                }

                is EateryApiResponse.Success -> _eateryDetailsViewState.update {
                    val currentMeal = userSelectedMeal ?: eatery.data.getCurrentDisplayedEvent()

                    EateryDetailViewState.Loaded(
                        mealToShow = MealViewState(
                            currentMeal?.startTimestamp,
                            currentMeal?.endTimestamp,
                            currentMeal?.menu?.map { menu ->
                                MenuCategoryViewState(
                                    menu.name ?: "",
                                    menu.items?.map { menuItem ->
                                        MenuItemViewState(
                                            item = menuItem,
                                            isFavorite = menuItem.name in favoriteItems
                                        )
                                    } ?: emptyList()
                                )
                            },
                            description = currentMeal?.type
                        ),
                        isFavorite = eatery.data.name in favoriteEateries,
                        eatery = eatery.data,
                        weekdayIndex = (it as? EateryDetailViewState.Loaded)?.weekdayIndex ?: 0
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleFavorite() {
        when (val eateryState = eateryDetailViewState.value) {
            is EateryDetailViewState.Loaded -> {
                viewModelScope.launch {
                    if (eateryState.isFavorite) {
                        userRepository.removeFavoriteEatery(eateryId)
                    } else {
                        userRepository.addFavoriteEatery(eateryId)
                    }
                }
            }

            else -> {
                // We cannot favorite an eatery that has not loaded yet
            }
        }
    }

    /**
     * Asynchronously toggles the favorite status of a menu item by its name
     */
    fun toggleFavoriteMenuItem(menuItem: String) {
        viewModelScope.launch {
            if (menuItem in userRepository.favoriteItemsFlow.value) {
                userRepository.removeFavoriteItem(menuItem)
            } else {
                userRepository.addFavoriteItem(menuItem)
            }
        }
    }

    fun sendReport(issue: String, report: String, eateryId: Int?) = viewModelScope.launch {
        userRepository.sendReport(issue, report, eateryId)
    }

    fun setSelectedWeekdayIndex(weekdayIndex: Int) {
        _eateryDetailsViewState.update {
            when (it) {
                is EateryDetailViewState.Loaded -> it.copy(
                    weekdayIndex = weekdayIndex
                )

                is EateryDetailViewState.Error -> it
                EateryDetailViewState.Loading -> it
            }
        }
    }

    /**
     * changes the value of _userSelectedMeal based on eatery, dayIndex, and mealDescription
     *
     * @param eatery the current eatery that eatery detail is displaying
     * @param dayIndex the index of the selected day, today is 0, tomorrow is 1, and so on
     * @param mealDescription, e.g. "lunch", "dinner", etc
     */
    fun selectEvent(eatery: Eatery, dayIndex: Int, mealDescription: String) {
        userSelectedMeal.update {
            eatery.getSelectedEvent(dayIndex, mealDescription)
        }
    }

    /**
     * resets the value of _userSelectedMeal to null
     */
    fun resetSelectedEvent() {
        userSelectedMeal.update { null }
    }

    /**
     * Sets the search query typed in by the user.
     */
    fun setSearchQuery(query: String) {
        _searchQueryFlow.value = query
    }
}

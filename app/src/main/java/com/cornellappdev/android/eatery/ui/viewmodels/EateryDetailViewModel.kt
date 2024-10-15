package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.MenuCategoryViewState
import com.cornellappdev.android.eatery.ui.components.general.MenuItemViewState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


sealed class EateryDetailViewState {
    object Loading : EateryDetailViewState()

    data class Loaded(
        val mealToShow: MealViewState
    ) : EateryDetailViewState()

    data class Error(val message: String) : EateryDetailViewState()
}


data class MealViewState(
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val menu: List<MenuCategoryViewState>
)

@HiltViewModel
class EateryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val eateryId: Int = checkNotNull(savedStateHandle["eateryId"])

    private val _eateryDetailsViewState =
        MutableStateFlow<EateryDetailViewState>(EateryDetailViewState.Loading)
    val eateryDetailViewState = _eateryDetailsViewState.asStateFlow()


    /**
     * A flow emitting the loading status of the current eatery.
     */
    lateinit var eateryFlow: StateFlow<EateryApiResponse<Eatery>>

    /**
     * A flow emitting the current or nearest meal of the selected eatery. You may assume this flow
     * is non-null if [eateryFlow] is emitting [EateryApiResponse.Success].
     */
    private lateinit var _curMeal: StateFlow<Event?>

    /**
     * A flow emitting the meal that the user has expressly selected, or null if the user hasn't
     * yet selected anything, in which case we should refer to [_curMeal].
     */
    private val userSelectedMeal = MutableStateFlow<Event?>(null)

    /**
     * A flow emitting the meal to show to the user. You can assume this will not be null if
     * [eateryFlow] is emitting [EateryApiResponse.Success].
     */
    lateinit var mealToShow: StateFlow<Event?>

    private val _searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * A flow emitting which search query the user has typed in.
     */
    val searchQueryFlow = this._searchQueryFlow.asStateFlow()

    // TODO: This sux lol lets change it to a flow somehow, probably like lateinit
    var isFavorite by mutableStateOf(false)
        private set

    init {
        openEatery()
    }

    /**
     * Initializes the eatery detail view model to the given eatery id. Initializes [eateryFlow] and
     * any other relevant flows originating from it.
     */
    private fun openEatery() {
        combine(
            userPreferencesRepository.favoritesFlow,
            userPreferencesRepository.favoriteItemsFlow,
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
                    ?: return@update EateryDetailViewState.Error("Meal not found")

                    EateryDetailViewState.Loaded(
                        mealToShow = MealViewState(
                            currentMeal.startTime,
                            currentMeal.endTime,
                            currentMeal.menu?.map {
                                MenuCategoryViewState(
                                    it.category ?: "",
                                    it.items?.map { menuItem ->
                                        MenuItemViewState(
                                            item = menuItem,
                                            isFavorite = favoriteItems[menuItem.name] ?: false
                                        )
                                    } ?: emptyList()
                                )
                            } ?: emptyList()
                        )
                    )
                }
            }
        }
        eateryFlow = eateryRepository.getEateryFlow(eateryId)
        isFavorite = userPreferencesRepository.favoritesFlow.value[eateryId] == true
    }

    fun toggleFavorite() {
        userPreferencesRepository.setFavorite(eateryId, !isFavorite)
        isFavorite = !isFavorite
    }

    fun sendReport(issue: String, report: String, eateryid: Int?) = viewModelScope.launch {
        userRepository.sendReport(issue, report, eateryid)
    }

    // TODO: Make function to allow user to select an event. When they do that, emit that event down
    //  _userSelectedMeal.

    /**
     * changes the value of _userSelectedMeal based on eatery, dayIndex, and mealDescription
     *
     * @param eatery the current eatery that eatery detail is displaying
     * @param dayIndex the index of the selected day, today is 0, tomorrow is 1, and so on
     * @param mealDescription, e.g. "lunch", "dinner", etc
     */
    fun selectEvent(eatery: Eatery, dayIndex: Int, mealDescription: String) {
        userSelectedMeal.value = eatery.getSelectedEvent(dayIndex, mealDescription)
    }

    /**
     * resets the value of _userSelectedMeal to null
     */
    fun resetSelectedEvent() {
        userSelectedMeal.value = null
    }

    /**
     * Sets the search query typed in by the user.
     */
    fun setSearchQuery(query: String) {
        _searchQueryFlow.value = query
    }
}

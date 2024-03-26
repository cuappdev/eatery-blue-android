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
import javax.inject.Inject

@HiltViewModel
class EateryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val eateryId: Int = checkNotNull(savedStateHandle["eateryId"])

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
    private lateinit var _userSelectedMeal: MutableStateFlow<Event?>

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
        openEatery(eateryId)
    }

    /**
     * Initializes the eatery detail view model to the given eatery id. Initializes [eateryFlow] and
     * any other relevant flows originating from it.
     */
    private fun openEatery(eateryId: Int) {
        eateryFlow = eateryRepository.getEateryFlow(eateryId)
        isFavorite = userPreferencesRepository.favoritesFlow.value[eateryId] == true

        // TODO: Initialize [_curMeal] as a map from [eatery]. This will become a flow that
        //  is tied to [eatery] and constantly emits its current meal.
        //  You'll probably want to use your [eatery.getCurrentDisplayedEvent()] from before.
        _curMeal = eateryFlow.map { eateryApiResponse ->
            when (eateryApiResponse) {
                is EateryApiResponse.Success -> eateryApiResponse.data.getCurrentDisplayedEvent()
                is EateryApiResponse.Pending -> null
                is EateryApiResponse.Error -> null
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

        _userSelectedMeal = MutableStateFlow(null)

        mealToShow = _curMeal.combine(_userSelectedMeal) { curr, userSelected ->

            // TODO: Implement the flow combine. (It's just emitting curr right now so it compiles)
            //  When userSelected is non null, emit that. Otherwise, default to emitting the current meal.
            //  This is the flow that we'll actually use to tell the screen which meal to show.
//            Log.d("UserSelectionCheck",(userSelected == null).toString())
            userSelected ?: curr
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
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
        _userSelectedMeal.value = eatery.getSelectedEvent(dayIndex, mealDescription)
    }

    /**
     * resets the value of _userSelectedMeal to null
     */
    fun resetSelectedEvent() {
        _userSelectedMeal.value = null
    }

    /**
     * Sets the search query typed in by the user.
     */
    fun setSearchQuery(query: String) {
        _searchQueryFlow.value = query
    }
}

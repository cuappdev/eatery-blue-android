package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.viewmodels.state.CompareMenusUIState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareMenusBotViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _compareMenusUiState = MutableStateFlow(CompareMenusUIState())
    val compareMenusUiState = _compareMenusUiState.asStateFlow()

    init {
        viewModelScope.launch {
            eateryRepository.homeEateryFlow.collect { apiResponse ->
                when (apiResponse) {
                    is EateryApiResponse.Success -> {
                        _compareMenusUiState.update { currentState ->
                            val allEateries = apiResponse.data.sortedBy { eatery ->
                                eatery.name
                            }.sortedBy { eatery ->
                                eatery.isClosed()
                            }

                            currentState.copy(
                                allEateries = allEateries,
                                eateries = filterEateries(
                                    allEateries,
                                    currentState.filters,
                                    currentState.selected
                                )
                            )
                        }
                    }

                    is EateryApiResponse.Error -> {
                    }

                    is EateryApiResponse.Pending -> {
                    }
                }
            }
        }
    }

    fun addSelected(eatery: Eatery) = viewModelScope.launch {
        _compareMenusUiState.update { currentState ->
            currentState.copy(
                selected = currentState.selected + listOf(eatery),
                eateries = filterEateries(
                    currentState.allEateries,
                    currentState.filters,
                    currentState.selected + listOf(eatery)
                )
            )
        }
    }

    fun removeSelected(eatery: Eatery) = viewModelScope.launch {
        _compareMenusUiState.update { currentState ->
            currentState.copy(
                selected = currentState.selected.filter { it != eatery },
                eateries = filterEateries(
                    currentState.allEateries,
                    currentState.filters,
                    currentState.selected.filter { it != eatery })
            )
        }
    }

    fun resetSelected() = viewModelScope.launch {
        _compareMenusUiState.update { currentState ->
            currentState.copy(
                selected = listOf(),
                eateries = filterEateries(currentState.allEateries, currentState.filters, listOf())
            )
        }
    }

    fun addFilterCM(filter: Filter) = viewModelScope.launch {
        _compareMenusUiState.update { currentState ->
            currentState.copy(
                filters = currentState.filters + listOf(filter),
                eateries = filterEateries(
                    currentState.allEateries,
                    currentState.filters + listOf(filter),
                    currentState.selected
                )
            )
        }
    }

    fun removeFilterCM(filter: Filter) = viewModelScope.launch {
        _compareMenusUiState.update { currentState ->
            currentState.copy(
                filters = currentState.filters.filter { it != filter },
                eateries = filterEateries(
                    currentState.allEateries,
                    currentState.filters.filter { it != filter },
                    currentState.selected
                )
            )
        }
    }

    private fun filterEateries(
        allEateries: List<Eatery>,
        filters: List<Filter>,
        selected: List<Eatery>
    ): List<Eatery> {
        return allEateries.filter { eatery ->
            passesFilter(eatery, filters, userPreferencesRepository.favoritesFlow.value, selected)
        }
    }

    private fun passesFilter(
        eatery: Eatery,
        filters: List<Filter>,
        favorites: Map<Int, Boolean>,
        selected: List<Eatery>?
    ): Boolean {
        var passesFilter = true
        if (filters.contains(Filter.UNDER_10)) {
            val walkTimes = eatery.getWalkTimes()
            passesFilter = walkTimes != null && walkTimes <= 10
        }

        if (filters.contains(Filter.FAVORITES)) {
            passesFilter = favorites[eatery.id] == true
        }

        val allLocationsValid =
            !filters.contains(Filter.NORTH) &&
                    !filters.contains(Filter.CENTRAL) &&
                    !filters.contains(Filter.WEST)

        if (filters.contains(Filter.SELECTED)) {
            if (!(selected?.contains(eatery) ?: false)) return false
        }

        // Passes filter if all locations aren't selected (therefore any location is valid, specified by allLocationsValid)
        // or one/multiple are selected and the eatery is located there.
        passesFilter = passesFilter &&
                (allLocationsValid || ((filters.contains(Filter.NORTH) && eatery.campusArea == "North") ||
                        (filters.contains(Filter.WEST) && eatery.campusArea == "West") ||
                        (filters.contains(Filter.CENTRAL) && eatery.campusArea == "Central")))

        val allPaymentMethodsValid =
            !filters.contains(Filter.CASH) &&
                    !filters.contains(Filter.BRB) &&
                    !filters.contains(Filter.SWIPES)

        // Passes filter if all three aren't selected (therefore any payment method is valid, specified by allPaymentMethodsValid)
        // or one/multiple are selected and the eatery takes it.
        return passesFilter &&
                (allPaymentMethodsValid || ((filters.contains(Filter.SWIPES) && eatery.paymentAcceptsMealSwipes == true) ||
                        (filters.contains(Filter.BRB) && eatery.paymentAcceptsBrbs == true) ||
                        (filters.contains(Filter.CASH) && eatery.paymentAcceptsCash == true)))
    }

    fun sendReport(issue: String, report: String, eateryid: Int?) = viewModelScope.launch {
        userRepository.sendReport(issue, report, eateryid)
    }


}

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
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    private val _filtersFlow: MutableStateFlow<List<Filter>> = MutableStateFlow(listOf())
    val filtersFlow = _filtersFlow.asStateFlow()

    /**
     * A flow emitting all eateries with the appropriate filters applied.
     */
    val eateryFlow: StateFlow<EateryApiResponse<List<Eatery>>> =
        eateryRepository.homeEateryFlow.combine(_filtersFlow) { apiResponse, filters ->
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

    // TODO: Favorites
    var favoriteEateries = mutableStateListOf<Eatery>()
        private set

    /**
     * A [StateFlow] that emits the 6 nearest eateries based on location.
     *
     * TODO: Walk times may not be updating automatically; may have to change location to use state.
     */
    val nearestEateries: StateFlow<List<Eatery>> = eateryFlow.map { apiResponse ->
        when (apiResponse) {
            is EateryApiResponse.Error -> listOf()
            is EateryApiResponse.Pending -> listOf()
            is EateryApiResponse.Success -> {
                apiResponse.data.sortedBy { it.getWalkTimes() }.slice(0..5)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    private var bigPopUp by mutableStateOf(false)

    fun setPopUp(bool: Boolean) {
        bigPopUp = bool
    }

    fun addFilter(filter: Filter) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.add(filter)
        _filtersFlow.value = newList
    }

    fun removeFilter(filter: Filter) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.remove(filter)
        _filtersFlow.value = newList
    }

    fun addPaymentMethodFilters(filters: List<Filter>) = viewModelScope.launch {
        val newList = _filtersFlow.value.toMutableList()
        newList.removeAll(Filter.PAYMENT_METHODS)
        newList.addAll(filters)
        _filtersFlow.value = newList
    }

    fun resetFilters() = viewModelScope.launch {
        _filtersFlow.value = listOf()
    }

    /**
     * Determines if the eatery passes the filter inspection based on what's currently selected.
     */
    private fun passesFilter(eatery: Eatery, filters: List<Filter>): Boolean {
        var passesFilter = true
        if (filters.contains(Filter.UNDER_10)) {
            val walkTimes = eatery.getWalkTimes()
            passesFilter = walkTimes != null && walkTimes <= 10
        }

        if (filters.contains(Filter.FAVORITES)) {
            passesFilter = favoriteEateries.any {
                it.id == eatery.id
            }
        }

        val allLocationsValid =
            !filters.contains(Filter.NORTH) &&
                    !filters.contains(Filter.CENTRAL) &&
                    !filters.contains(Filter.WEST)

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

    fun addFavorite(eateryId: Int?) = viewModelScope.launch {
        // TODO: Fix favoriting flow.
    }

    fun removeFavorite(eateryId: Int?) = viewModelScope.launch {
        // TODO: Fix favoriting flow.
    }

    fun getNotificationFlowCompleted() = runBlocking {
        return@runBlocking userPreferencesRepository.getNotificationFlowCompleted()
    }

    fun setNotificationFlowCompleted(value: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setNotificationFlowCompleted(value)
    }
}

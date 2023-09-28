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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

    var nearestEateries = mutableStateListOf<Eatery>()
        private set

    var filteredResults = mutableStateListOf<Eatery>()
        private set

    var bigPopUp by mutableStateOf(false)

    fun setPopUp(bool: Boolean) {
        bigPopUp = bool
    }

    init {
        queryAllEateries()
    }

    fun queryAllEateries() = viewModelScope.launch {
        try {
            val eateryResponse = eateryRepository.getHomeEateries()
            _allEateries.addAll(eateryResponse)

            val favoriteEateriesIds =
                userPreferencesRepository.getFavoritesMap().keys
            favoriteEateries.addAll(_allEateries.filter {
                favoriteEateriesIds.contains(it.id)
            })

            eateryRetrievalState = EateryRetrievalState.Success
        } catch (e: Exception) {
            eateryRetrievalState = EateryRetrievalState.Error
        }
    }

    fun updateFavorites() = viewModelScope.launch {
        val favoriteEateriesKeys =
            userPreferencesRepository.getFavoritesMap().keys
        favoriteEateries = _allEateries.filter {
            favoriteEateriesKeys.contains(it.id)
        }.toCollection(mutableStateListOf())
    }

    fun updateNearest() = viewModelScope.launch {
        var eateries = _allEateries.sortedBy { it.getWalkTimes() }
        nearestEateries = eateries.slice(0..5).toCollection(
            mutableStateListOf()
        )
    }

    fun addFilter(filter: Filter) = viewModelScope.launch {
        _currentFiltersSelected.add(filter)
        filterEateries()
    }

    fun removeFilter(filter: Filter) = viewModelScope.launch {
        _currentFiltersSelected.remove(filter)
        filterEateries()
    }

    fun addPaymentMethodFilters(filters: List<Filter>) = viewModelScope.launch {
        _currentFiltersSelected.removeAll(Filter.PAYMENT_METHODS)
        _currentFiltersSelected.addAll(filters)
        filterEateries()
    }

    fun resetFilters() = viewModelScope.launch {
        _currentFiltersSelected.clear()
    }

    private fun filterEateries() = viewModelScope.launch {
        filteredResults = _allEateries.filter { eatery ->
            passesFilter(eatery)
        }.toCollection(mutableStateListOf())
    }

    /**
     * Determines if the eatery passes the filter inspection based on what's currently selected.
     */
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

        val allPaymentMethodsValid =
            !_currentFiltersSelected.contains(Filter.CASH) &&
                    !_currentFiltersSelected.contains(Filter.BRB) &&
                    !_currentFiltersSelected.contains(Filter.SWIPES)

        // Passes filter if all three aren't selected (therefore any payment method is valid, specified by allPaymentMethodsValid)
        // or one/multiple are selected and the eatery takes it.
        return passesFilter &&
                (allPaymentMethodsValid || ((_currentFiltersSelected.contains(Filter.SWIPES) && eatery.paymentAcceptsMealSwipes == true) ||
                        (_currentFiltersSelected.contains(Filter.BRB) && eatery.paymentAcceptsBrbs == true) ||
                        (_currentFiltersSelected.contains(Filter.CASH) && eatery.paymentAcceptsCash == true)))
    }

    fun addFavorite(eateryId: Int?) = viewModelScope.launch {
        if (eateryId == null) return@launch

        userPreferencesRepository.setFavorite(eateryId, true)

        _allEateries.firstOrNull { eatery ->
            eatery.id == eateryId
        }?.let { matchingEatery -> favoriteEateries.add(matchingEatery) }
    }

    fun removeFavorite(eateryId: Int?) = viewModelScope.launch {
        if (eateryId == null) return@launch

        userPreferencesRepository.setFavorite(eateryId, false)
        favoriteEateries.removeIf { eatery ->
            eatery.id == eateryId
        }
    }

    fun getNotificationFlowCompleted() = runBlocking {
        return@runBlocking userPreferencesRepository.getNotificationFlowCompleted()
    }

    fun setNotificationFlowCompleted(value: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setNotificationFlowCompleted(value)
    }
}

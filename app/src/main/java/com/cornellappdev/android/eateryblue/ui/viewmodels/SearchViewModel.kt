package com.cornellappdev.android.eateryblue.ui.viewmodels

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    private val _filtersFlow: MutableStateFlow<List<Filter>> = MutableStateFlow(listOf())

    /**
     * A flow of filters applied to the screen.
     */
    val filtersFlow = _filtersFlow.asStateFlow()

    /**
     * The current search query. Combine with other flows to filter by search query.
     */
    private val _searchFlow: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * The current String search query.
     */
    val searchFlow = _searchFlow.asStateFlow()

    /**
     * A flow of the eateries that should show up with the current query.
     */
    val searchResultEateries = combine(
        eateryRepository.homeEateryFlow,
        filtersFlow,
        _searchFlow
    ) { eateryApiResponse, filters, searchQuery ->
        when (eateryApiResponse) {
            is EateryApiResponse.Error -> EateryApiResponse.Error
            is EateryApiResponse.Pending -> EateryApiResponse.Pending
            is EateryApiResponse.Success -> {
                EateryApiResponse.Success(
                    eateryApiResponse.data.sortedBy { it.isClosed() }.filter {
                        it.passesFilter(filters) && it.passesSearch(searchQuery)
                    })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EateryApiResponse.Pending)

    /**
     * A flow of the user's recent searches.
     */
    val recentSearches = userPreferencesRepository.recentSearchesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    /**
     * A flow of the user's current favorite eateries.
     */
    val favoriteEateries =
        combine(
            eateryRepository.homeEateryFlow,
            userPreferencesRepository.favoritesFlow
        ) { apiResponse, favorites ->
            when (apiResponse) {
                is EateryApiResponse.Error -> listOf()
                is EateryApiResponse.Pending -> listOf()
                is EateryApiResponse.Success -> {
                    apiResponse.data.filter {
                        favorites[it.id] == true
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun addPaymentMethodFilters(filters: List<Filter>) {
        val newList = _filtersFlow.value.toMutableList()
        newList.removeAll(filters)
        newList.addAll(filters)
        _filtersFlow.value = newList
    }

    fun addFilter(filter: Filter) {
        val newList = _filtersFlow.value.toMutableList()
        newList.add(filter)
        _filtersFlow.value = newList
    }

    fun removeFilter(filter: Filter) {
        val newList = _filtersFlow.value.toMutableList()
        newList.remove(filter)
        _filtersFlow.value = newList
    }

    fun queryEateries(query: String) {
        _searchFlow.value = query
    }

    private fun Eatery.passesFilter(filters: List<Filter>): Boolean {
        var passesFilter = true
        if (filters.contains(Filter.UNDER_10)) {
            val walkTimes = getWalkTimes()
            passesFilter = walkTimes != null && walkTimes <= 10
        }

        if (filters.contains(Filter.FAVORITES)) {
            passesFilter = favoriteEateries.value.contains(this) == true
        }

        val allLocationsValid =
            !filters.contains(Filter.NORTH) &&
                    !filters.contains(Filter.CENTRAL) &&
                    !filters.contains(Filter.WEST)

        // Passes filter if all locations aren't selected (therefore any location is valid, specified by allLocationsValid)
        // or one/multiple are selected and the eatery is located there.
        passesFilter = passesFilter &&
                (allLocationsValid || ((filters.contains(Filter.NORTH) && campusArea == "North") ||
                        (filters.contains(Filter.WEST) && campusArea == "West") ||
                        (filters.contains(Filter.CENTRAL) && campusArea == "Central")))

        val allPaymentMethodsValid =
            !filters.contains(Filter.CASH) &&
                    !filters.contains(Filter.BRB) &&
                    !filters.contains(Filter.SWIPES)

        // Passes filter if all three aren't selected (therefore any payment method is valid, specified by allPaymentMethodsValid)
        // or one/multiple are selected and the eatery takes it.
        return passesFilter &&
                (allPaymentMethodsValid || ((filters.contains(Filter.SWIPES) && paymentAcceptsMealSwipes == true) ||
                        (filters.contains(Filter.BRB) && paymentAcceptsBrbs == true) ||
                        (filters.contains(Filter.CASH) && paymentAcceptsCash == true)))
    }

    private fun Eatery.passesSearch(query: String): Boolean {
        // TODO: We might want to make an asynchronous search repository, cuz this search is SLOW.
        if (query.isEmpty()) return false
        val nameMatch = name?.contains(query, true) ?: false
        val menuMatch = events?.any { event ->
            event.menu?.any { menuCategory ->
                menuCategory.items?.any { it.name?.contains(query, true) ?: false } ?: false
            } ?: false
        } ?: false
        return nameMatch || menuMatch
    }

    fun addFavorite(eateryId: Int?) {
        if (eateryId != null)
            userPreferencesRepository.setFavorite(eateryId, true)
    }

    fun removeFavorite(eateryId: Int?) {
        if (eateryId != null)
            userPreferencesRepository.setFavorite(eateryId, false)
    }

    fun addRecentSearch(eateryId: Int?) = viewModelScope.launch {
        userPreferencesRepository.addRecentSearch(eateryId ?: 0)
    }

    fun openEatery(eateryId: Int) = eateryRepository.getEateryState(eateryId)

}

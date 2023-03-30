package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.repositories.EateryRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.ui.components.general.Filter
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.SearchRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    private val _allEateries = mutableSetOf<Eatery>()
    val allEateries: Set<Eatery> = _allEateries

    private val _currentFiltersSelected = mutableStateListOf<Filter>()
    val currentFiltersSelected: List<Filter> = _currentFiltersSelected

    var favoriteEateries = mutableStateListOf<Eatery>()
        private set

    var recentSearches = mutableStateListOf<Int>()
        private set

    var searchResultState: SearchRetrievalState by mutableStateOf(SearchRetrievalState.Pending)
        private set

    var eateryRetrievalState: EateryRetrievalState by mutableStateOf(EateryRetrievalState.Pending)
        private set

    var searchText by mutableStateOf("")
        private set

    var firstLaunch by mutableStateOf(true)

    init {
        firstLaunch = true
        queryAllEateries()
    }

    fun queryAllEateries() = viewModelScope.launch {
        try {
            val eateryResponse = eateryRepository.getAllEateries()
            _allEateries.addAll(eateryResponse)
//            if (eateryResponse.success) {
//                eateryResponse.data?.let {
//                    _allEateries.addAll(it)
//                    searchResultState = SearchRetrievalState.Success(it)
//                }

            val favoriteEateriesIds =
                userPreferencesRepository.getFavoritesMap().keys
            favoriteEateries.addAll(allEateries.filter {
                favoriteEateriesIds.contains(it.id)
            })

            recentSearches = userPreferencesRepository.getRecentSearches().toMutableStateList()
            eateryRetrievalState = EateryRetrievalState.Success
            //}
        } catch (_: Exception) {
            eateryRetrievalState = EateryRetrievalState.Error
        }
    }

    fun addPaymentMethodFilters(filters: List<Filter>) = viewModelScope.launch {
        _currentFiltersSelected.removeAll(Filter.PAYMENT_METHODS)
        _currentFiltersSelected.addAll(filters)
        updateSearchResults()
    }

    fun addFilter(filter: Filter) = viewModelScope.launch {
        _currentFiltersSelected.add(filter)
        updateSearchResults()
    }

    fun removeFilter(filter: Filter) = viewModelScope.launch {
        _currentFiltersSelected.remove(filter)
        updateSearchResults()
    }

    fun queryEateries(query: String) = viewModelScope.launch {
        searchText = query
        updateSearchResults()
    }

    fun updateSearchResults() = viewModelScope.launch {
        searchResultState = SearchRetrievalState.Pending
        // Uses the currentSearchText and filters to get searchResults
    }

    fun updateFavorites() = viewModelScope.launch {
        val favoriteEateriesIds =
            userPreferencesRepository.getFavoritesMap().keys
        favoriteEateries = allEateries.filter {
            favoriteEateriesIds.contains(it.id)
        }.toCollection(mutableStateListOf())
    }
}

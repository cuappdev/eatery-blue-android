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
     * The current search query. Private. Combine with other flows to filter by search query.
     */
    private val searchFlow: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * A flow of the eateries that should show up with the current query.
     */
    val searchResultEateries = combine(
        eateryRepository.homeEateryFlow,
        filtersFlow,
        searchFlow
    ) { eateryApiResponse, filters, searchQuery ->
        when (eateryApiResponse) {
            is EateryApiResponse.Error -> EateryApiResponse.Error
            is EateryApiResponse.Pending -> EateryApiResponse.Pending
            is EateryApiResponse.Success -> {
                EateryApiResponse.Success(
                    eateryApiResponse.data.filter {
                        it.passesFilter(filters) && it.passesSearch(searchQuery)
                    })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EateryApiResponse.Pending)

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
        searchFlow.value = query
    }

    private fun Eatery.passesFilter(filters: List<Filter>): Boolean {
        // TODO: Implement filtering logic
        return true
    }

    private fun Eatery.passesSearch(query: String): Boolean {
        if (query.isEmpty()) return false

        // TODO: Add searching logic based on the input query.
        return true
    }
}

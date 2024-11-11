package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.EateryStatus
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.Filter.FromEateryFilter
import com.cornellappdev.android.eatery.ui.components.general.FilterData
import com.cornellappdev.android.eatery.ui.screens.ItemFavoritesCardViewState
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

private val allEateryFilters = listOf(
    FromEateryFilter.Central,
    FromEateryFilter.North,
    FromEateryFilter.West,
    FromEateryFilter.Swipes,
    FromEateryFilter.BRB,
    FromEateryFilter.Under10,
    FromEateryFilter.Cash
)
private val allItemFilters = listOf(
    Filter.ItemAvailableToday,
    FromEateryFilter.Central,
    FromEateryFilter.North,
    FromEateryFilter.West,
)

sealed class FavoritesScreenViewState {
    data class Loaded(
        val eateries: List<Eatery>,
        val favoriteCards: List<ItemFavoritesCardViewState>,
        val selectedEateryFilters: List<FromEateryFilter>,
        val selectedItemFilters: List<Filter>,
        val eateryFilters: List<Filter> = allEateryFilters,
        val itemFilters: List<Filter> = allItemFilters,
    ) : FavoritesScreenViewState()

    data object Error : FavoritesScreenViewState()
    data object Loading : FavoritesScreenViewState()
}


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    eateryRepository: EateryRepository
) : ViewModel() {
    private val selectedEateryFiltersFlow = MutableStateFlow<List<FromEateryFilter>>(emptyList())
    private val selectedItemFiltersFlow = MutableStateFlow<List<Filter>>(emptyList())

    /**
     * A flow emitting the latest UI state
     */
    val favoritesScreenViewState: StateFlow<FavoritesScreenViewState> = combine(
        eateryRepository.eateryFlow,
        userPreferencesRepository.favoriteItemsFlow,
        selectedEateryFiltersFlow,
        selectedItemFiltersFlow
    ) { apiResponse, favoriteItemsMap, selectedEateryFilters, selectedItemFilters ->
        when (apiResponse) {
            is EateryApiResponse.Error -> FavoritesScreenViewState.Error
            is EateryApiResponse.Pending -> FavoritesScreenViewState.Loading
            is EateryApiResponse.Success -> {
                val allEateries = apiResponse.data

                val filteredEateries = apiResponse.data.filter {
                    Filter.passesSelectedFilters(
                        allEateryFilters, selectedEateryFilters, FilterData(
                            eatery = it,
                        )
                    )
                }


                val favoriteItems = favoriteItemsMap.keys.filter { favoriteItemsMap[it] == true }

                val menuItemsToEateries: Map<String, List<Eatery>> =
                    favoriteItems.associateWith { itemName ->
                        allEateries.filter { eatery ->
                            val todayEvents = eatery.events?.filter {
                                (it.endTime ?: LocalDateTime.MAX) < LocalDateTime.now().withHour(23)
                                    .withMinute(59)
                            }
                            todayEvents?.any { event ->
                                event.menu?.flatMap { it.items ?: emptyList() }?.any {
                                    itemName == it.name
                                } == true
                            } == true
                        }
                    }.mapValues { (itemName, eateries) ->
                        eateries.filter {
                            Filter.passesSelectedFilters(
                                allItemFilters, selectedItemFilters, FilterData(
                                    eatery = it,
                                    targetItemName = itemName,
                                )
                            )
                        }
                    }.filter { (_, eateries) ->
                        if (Filter.ItemAvailableToday in selectedItemFilters) {
                            eateries.isNotEmpty()
                        } else true
                    }

                val itemFavoriteCards = menuItemsToEateries.map { (itemName, eateriesByItem) ->
                    ItemFavoritesCardViewState(
                        itemName = itemName,
                        availability = if (eateriesByItem.isEmpty()) EateryStatus(
                            "Not available",
                            GrayThree
                        ) else EateryStatus("Available today", Green),
                        mealAvailability = eateriesByItem.groupBy { eatery ->
                            eatery.events?.find { event ->
                                event.menu?.any {
                                    it.items?.any { menuItem -> menuItem.name == itemName } == true
                                } == true
                            }?.description
                        }.mapValues { mapEntry ->
                            mapEntry.value.mapNotNull { eatery -> eatery.name }
                        }.mapKeys { (key, _) ->
                            if (key == null || key !in listOf(
                                    "Breakfast",
                                    "Lunch",
                                    "Late lunch",
                                    "Brunch",
                                    "Dinner"
                                )
                            ) "Other" else key
                        }.toSortedMap { s1, s2 -> s1.toSortOrder().compareTo(s2.toSortOrder()) }
                    )
                }.sortedByDescending { it.availability.statusColor == Green }


                FavoritesScreenViewState.Loaded(
                    eateries = filteredEateries,
                    favoriteCards = itemFavoriteCards,
                    selectedItemFilters = selectedItemFilters,
                    selectedEateryFilters = selectedEateryFilters,
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesScreenViewState.Loading)

    private fun String.toSortOrder(): Int = when (this) {
        "Breakfast" -> 1
        "Brunch" -> 2
        "Lunch" -> 3
        "Late lunch" -> 4
        "Dinner" -> 5
        else -> Int.MAX_VALUE
    }

    fun removeFavoriteMenuItem(menuItemName: String) = viewModelScope.launch {
        userPreferencesRepository.toggleFavoriteMenuItem(menuItemName)
    }

    fun toggleEateryFilter(filter: Filter.FromEateryFilter) {
        selectedEateryFiltersFlow.update {
            if (filter in it) it - filter else it + filter
        }
    }

    fun toggleItemFilter(filter: Filter) {
        selectedItemFiltersFlow.update {
            if (filter in it) it - filter else it + filter
        }
    }

    fun removeFavorite(eateryId: Int?) {
        if (eateryId != null) userPreferencesRepository.setFavorite(eateryId, false)
    }
}

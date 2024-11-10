package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.EateryStatus
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.passesFilter
import com.cornellappdev.android.eatery.ui.screens.ItemFavoritesCardViewState
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import javax.inject.Inject

sealed class FavoritesScreenViewState {
    data class Loaded(
        val eateries: List<Eatery>,
        val favoriteCards: List<ItemFavoritesCardViewState>,
    ) : FavoritesScreenViewState()

    data object Error : FavoritesScreenViewState()
    data object Loading : FavoritesScreenViewState()
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    eateryRepository: EateryRepository
) : ViewModel() {

    private val _selectedFiltersFlow = MutableStateFlow(emptyList<Filter>())
    val selectedFiltersFlow = _selectedFiltersFlow.asStateFlow()

    /**
     * A flow emitting the latest UI state
     */
    val favoritesScreenViewState: StateFlow<FavoritesScreenViewState> = combine(
        eateryRepository.eateryFlow, userPreferencesRepository.favoritesFlow,
        userPreferencesRepository.favoriteItemsFlow,
        _selectedFiltersFlow
    ) { apiResponse, favorites, favoriteItemsMap, filters ->
        when (apiResponse) {
            is EateryApiResponse.Error -> FavoritesScreenViewState.Error
            is EateryApiResponse.Pending -> FavoritesScreenViewState.Loading
            is EateryApiResponse.Success -> {
                val favoriteEateries = apiResponse.data.filter {
                    favorites[it.id] == true
                }.sortedBy { it.name }.sortedBy { it.isClosed() }
                val allEateries = apiResponse.data

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
                    }.mapValues {eateryList->
                        eateryList.value.filter{ eatery ->
                            filters.all{ filter ->
                                filter.passesFilter(eatery, emptyMap(), emptyList())
                            }
                        }
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
                        }
                            .mapValues { mapEntry ->
                                mapEntry.value.mapNotNull { eatery -> eatery.name }
                            }
                            .mapKeys { (key, _) ->
                                if (key == null || key !in listOf(
                                        "Breakfast",
                                        "Lunch",
                                        "Dinner"
                                    )
                                ) "Other" else key
                            }
                    )
                }.sortedByDescending { it.availability.statusColor == Green }


                FavoritesScreenViewState.Loaded(
                    eateries = favoriteEateries,
                    favoriteCards = itemFavoriteCards
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesScreenViewState.Loading)

    fun removeFavorite(eateryId: Int?) {
        if (eateryId != null) userPreferencesRepository.setFavorite(eateryId, false)
    }

    fun toggleFilter(filter: Filter) {
        _selectedFiltersFlow.update { if (filter in it) it - filter else it + filter }
    }
}

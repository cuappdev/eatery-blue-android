package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterData
import com.cornellappdev.android.eatery.ui.viewmodels.state.CompareMenusUIState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareMenusBotViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _compareMenusUiState = MutableStateFlow(CompareMenusUIState())
    val compareMenusUiState: StateFlow<CompareMenusUIState> = _compareMenusUiState.asStateFlow()

    val compareMenusBottomSheetFilters = listOf(
        Filter.Selected,
        Filter.FromEateryFilter.North,
        Filter.FromEateryFilter.West,
        Filter.FromEateryFilter.Central,
        Filter.FromEateryFilter.Under10,
    )

    private val filtersFlow = MutableStateFlow(emptyList<Filter>())
    private val selectedEateriesFlow = MutableStateFlow(emptyList<Eatery>())

    private var firstEatery: Eatery? = null

    val eateryFlow: StateFlow<EateryApiResponse<List<Eatery>>> =
        eateryRepository.homeEateryFlow.map { apiResponse ->
            when (apiResponse) {
                is EateryApiResponse.Error -> EateryApiResponse.Error
                is EateryApiResponse.Pending -> EateryApiResponse.Pending
                is EateryApiResponse.Success -> {
                    EateryApiResponse.Success(
                        apiResponse.data.sortedBy { eatery ->
                            eatery.name
                        }.sortedBy { eatery ->
                            eatery.isClosed()
                        })
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, EateryApiResponse.Pending)

    init {
        combine(
            eateryFlow,
            userPreferencesRepository.favoritesFlow,
            filtersFlow,
            selectedEateriesFlow
        ) { eateriesApiResponse, favorites, filters, selected ->
            when (eateriesApiResponse) {
                is EateryApiResponse.Success -> {
                    _compareMenusUiState.update { currentState ->
                        currentState.copy(
                            eateries =
                            (listOfNotNull(firstEatery) + eateriesApiResponse.data.filter { it.name != firstEatery?.name }).filter { eatery ->
                                Filter.passesSelectedFilters(
                                    compareMenusBottomSheetFilters,
                                    filters,
                                    FilterData(
                                        eatery = eatery,
                                        selectedEateryIds = selected.mapNotNull { it.id })
                                )
                            },
                            allEateries = listOfNotNull(firstEatery) + eateriesApiResponse.data.filter { it.name != firstEatery?.name },
                            selected = selected,
                            filters = filters,
                        )
                    }
                }

                is EateryApiResponse.Error -> {
                }

                is EateryApiResponse.Pending -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addSelected(eatery: Eatery) = viewModelScope.launch {
        selectedEateriesFlow.update {
            it + eatery
        }
    }

    fun removeSelected(eatery: Eatery) = viewModelScope.launch {
        selectedEateriesFlow.update {
            it - eatery
        }
    }

    fun resetSelected() = viewModelScope.launch {
        selectedEateriesFlow.update {
            emptyList()
        }
    }

    fun addFilterCM(filter: Filter) = viewModelScope.launch {
        filtersFlow.update { filters ->
            when (filter) {
                Filter.FromEateryFilter.North ->
                    filters.filter { it != Filter.FromEateryFilter.West && it != Filter.FromEateryFilter.Central } + filter

                Filter.FromEateryFilter.West ->
                    filters.filter { it != Filter.FromEateryFilter.North && it != Filter.FromEateryFilter.Central } + filter

                Filter.FromEateryFilter.Central ->
                    filters.filter { it != Filter.FromEateryFilter.West && it != Filter.FromEateryFilter.North } + filter

                else ->
                    filters + filter
            }
        }
    }

    fun removeFilterCM(filter: Filter) = viewModelScope.launch {
        filtersFlow.update {
            it - filter
        }
    }

    fun sendReport(issue: String, report: String, eateryid: Int?) = viewModelScope.launch {
        userRepository.sendReport(issue, report, eateryid)
    }

    fun initializedFirstEatery(
        eatery: Eatery?
    ) {
        firstEatery = eatery
        if (eatery != null) {
            selectedEateriesFlow.update {
                it + eatery
            }
        }
    }

}

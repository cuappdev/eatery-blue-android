package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.passesFilter
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _compareMenusUiState = MutableStateFlow(CompareMenusUIState())
    val compareMenusUiState: StateFlow<CompareMenusUIState> = _compareMenusUiState.asStateFlow()

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
                            eateries = if (firstEatery != null) {
                                (listOf(firstEatery!!) + (eateriesApiResponse.data.filter { eatery ->
                                    eatery.name != firstEatery!!.name && filters.all { filter ->
                                        filter.passesFilter(eatery, favorites, selected)
                                    }
                                }))
                            } else {
                                eateriesApiResponse.data.filter { eatery ->
                                    filters.all { filter ->
                                        filter.passesFilter(eatery, favorites, selected)
                                    }
                                }
                            },
                            allEateries = if (firstEatery != null) {
                                listOf(firstEatery!!) + eateriesApiResponse.data.filter { it.name != firstEatery!!.name }
                            } else {
                                eateriesApiResponse.data
                            },
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
                Filter.NORTH ->
                    filters.filter { it != Filter.WEST && it != Filter.CENTRAL } + filter

                Filter.WEST ->
                    filters.filter { it != Filter.NORTH && it != Filter.CENTRAL } + filter

                Filter.CENTRAL ->
                    filters.filter { it != Filter.WEST && it != Filter.NORTH } + filter

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

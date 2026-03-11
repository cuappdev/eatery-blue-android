package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterData
import com.cornellappdev.android.eatery.ui.components.general.updateFilters
import com.cornellappdev.android.eatery.ui.viewmodels.state.CompareMenusUIState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
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
    eateryRepository: EateryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _error = MutableStateFlow<NetworkUiError?>(null)
    val error = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

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
        eateryRepository.eateryFlow.map { apiResponse ->
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
            filtersFlow,
            selectedEateriesFlow
        ) { eateriesApiResponse, filters, selected ->
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

    fun toggleFilter(filter: Filter) {
        filtersFlow.update {
            it.updateFilters(filter)
        }
    }

    fun sendReport(issue: String, report: String, eateryid: Int?) = viewModelScope.launch {
        when (val result = userRepository.sendReport(issue, report, eateryid)) {
            is Result.Success -> {
                _error.value = null
            }

            is Result.Error -> {
                _error.value = NetworkUiError.Failed(NetworkAction.SendReport, result.error)
            }
        }
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

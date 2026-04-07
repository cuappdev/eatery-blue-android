package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompareMenusViewModel @Inject constructor(
    eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    data class CompareMenusUiState(
        val eateries: List<Eatery> = emptyList(),
        val events: List<Event?> = emptyList(),
        val error: NetworkUiError? = null
    )

    private val _error = MutableStateFlow<NetworkUiError?>(null)

    private val _selectedEateryIds = MutableStateFlow<Set<Int>>(emptySet())

    val uiState: StateFlow<CompareMenusUiState> = combine(
        eateryRepository.eateryFlow,
        _selectedEateryIds,
        _error
    ) { apiResponse, eateryIds, error ->
        val eateries = when (apiResponse) {
            is EateryApiResponse.Success -> apiResponse.data.filter { it.id in eateryIds }
            else -> emptyList()
        }
        CompareMenusUiState(
            eateries = eateries,
            events = eateries.map { it.getCurrentDisplayedEvent() },
            error = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CompareMenusUiState())

    fun clearError() {
        _error.value = null
    }

    fun openEatery(eateryIds: List<Int>) {
        _selectedEateryIds.value = eateryIds.toSet()
    }

    suspend fun sendReport(issue: String, report: String, eateryId: Int?): Boolean {
        when (val result = userRepository.sendReport(issue, report, eateryId)) {
            is Result.Success -> {
                _error.value = null
                return true
            }

            is Result.Error -> {
                _error.value = NetworkUiError.Failed(NetworkAction.SendReport, result.error)
                return false
            }
        }
    }
}

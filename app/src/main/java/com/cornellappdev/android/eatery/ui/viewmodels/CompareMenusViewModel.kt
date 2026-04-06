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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CompareMenusViewModel @Inject constructor(
    eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _error = MutableStateFlow<NetworkUiError?>(null)
    val error = _error.asStateFlow()

    private val _eateryFlow = MutableStateFlow<List<Eatery>>(emptyList())
    val eateryFlow = _eateryFlow.asStateFlow()

    private val _eventFlow = MutableStateFlow<List<Event?>>(emptyList())
    val eventFlow = _eventFlow.asStateFlow()

    private val _selectedEateryIds = MutableStateFlow<Set<Int>>(emptySet())

    init {
        eateryRepository.eateryFlow
            .combine(_selectedEateryIds) { apiResponse, eateryIds ->
                when (apiResponse) {
                    is EateryApiResponse.Success -> apiResponse.data.filter { it.id in eateryIds }
                    else -> emptyList()
                }
            }
            .onEach { eateries ->
                _eateryFlow.value = eateries
                _eventFlow.value = eateries.map { it.getCurrentDisplayedEvent() }
            }
            .launchIn(viewModelScope)
    }

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

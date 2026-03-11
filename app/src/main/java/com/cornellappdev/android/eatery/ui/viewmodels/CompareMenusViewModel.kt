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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareMenusViewModel @Inject constructor(
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _error = MutableStateFlow<NetworkUiError?>(null)
    val error = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    lateinit var eateryFlow: StateFlow<List<Eatery>>
    lateinit var eventFlow: StateFlow<List<Event?>>

    fun openEatery(eateryIds: List<Int>) {
        eateryFlow = eateryRepository.eateryFlow.map { apiResponse ->
            when (apiResponse) {
                is EateryApiResponse.Success -> {
                    apiResponse.data.filter { eateryIds.contains(it.id) }
                }

                else -> emptyList()
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        eventFlow = eateryFlow.map { eateries ->
            eateries.map { eatery ->
                eatery.getCurrentDisplayedEvent()
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun sendReport(issue: String, report: String, eateryId: Int?) = viewModelScope.launch {
        when (val result = userRepository.sendReport(issue, report, eateryId)) {
            is Result.Success -> {
                _error.value = null
            }

            is Result.Error -> {
                _error.value = NetworkUiError.Failed(NetworkAction.SendReport, result.error)
            }
        }
    }
}

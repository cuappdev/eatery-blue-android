package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState.asStateFlow()

    sealed class ReportState {
        object Idle : ReportState()
        object Sending : ReportState()
        object Success : ReportState()
        data class Error(val error: NetworkUiError) : ReportState()
    }

    fun sendReport(issue: String, report: String) = viewModelScope.launch {
        _reportState.value = ReportState.Sending
        when (val result = userRepository.sendReport(issue, report, null)) {
            is Result.Success -> {
                _reportState.value = ReportState.Success
            }

            is Result.Error -> {
                _reportState.value =
                    ReportState.Error(NetworkUiError.Failed(NetworkAction.SendReport, result.error))
            }
        }
    }

    fun resetReportState() {
        _reportState.value = ReportState.Idle
    }
}

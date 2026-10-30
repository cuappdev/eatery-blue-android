package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import com.cornellappdev.android.eatery.ui.viewmodels.state.ReportUiState
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
    private val _reportState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val reportState: StateFlow<ReportUiState> = _reportState.asStateFlow()

    fun clearReportState() {
        _reportState.value = ReportUiState.Idle
    }

    fun sendReport(issue: String, report: String) {
        viewModelScope.launch {
            _reportState.value = ReportUiState.Sending
            when (val result = userRepository.sendReport(issue, report, null)) {
                is Result.Success -> {
                    _reportState.value = ReportUiState.Success
                }

                is Result.Error -> {
                    _reportState.value =
                        ReportUiState.Error(
                            NetworkUiError.Failed(
                                NetworkAction.SendReport,
                                result.error
                            )
                        )
                }
            }
        }
    }
}

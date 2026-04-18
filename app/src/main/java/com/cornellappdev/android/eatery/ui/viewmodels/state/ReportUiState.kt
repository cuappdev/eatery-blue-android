package com.cornellappdev.android.eatery.ui.viewmodels.state

sealed class ReportUiState {
    object Idle : ReportUiState()
    object Sending : ReportUiState()
    object Success : ReportUiState()
    data class Error(val error: NetworkUiError) : ReportUiState()
}


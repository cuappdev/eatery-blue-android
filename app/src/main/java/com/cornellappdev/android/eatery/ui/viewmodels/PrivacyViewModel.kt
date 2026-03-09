package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val analyticsDisabledFlow: StateFlow<Boolean> =
        userPreferencesRepository.analyticsDisabledFlow
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val analyticsDisabled: Boolean
        get() = analyticsDisabledFlow.value

    fun setAnalyticsDisabled(analyticsDisabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setAnalyticsDisabled(analyticsDisabled)
    }
}

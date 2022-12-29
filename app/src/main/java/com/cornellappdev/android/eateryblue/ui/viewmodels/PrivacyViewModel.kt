package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    var analyticsDisabled: Boolean by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            analyticsDisabled = userPreferencesRepository.getAnalyticsDisabled()
        }
    }

    fun setAnalyticsDisabled(analyticsDisabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.setAnalyticsDisabled(analyticsDisabled)
        this@PrivacyViewModel.analyticsDisabled = analyticsDisabled
    }
}

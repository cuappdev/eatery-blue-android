package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.ThemePreference
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    val themePreference: StateFlow<ThemePreference> = repository.themePreferenceFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ThemePreference.SYSTEM
    )

    fun enableDarkMode() {
        viewModelScope.launch { repository.setDarkMode(true) }
    }

    fun enableLightMode() {
        viewModelScope.launch { repository.setDarkMode(false) }
    }

    fun enableSystemMode() {
        viewModelScope.launch { repository.setSystemMode() }
    }
}

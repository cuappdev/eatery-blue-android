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
class ThemeViewModel @Inject constructor(
    private val repository: UserPreferencesRepository): ViewModel()
{
    val isDarkMode: StateFlow<Boolean?> = repository.isDarkModeFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedInFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun toggleDarkMode()
    {
        viewModelScope.launch { repository.setDarkMode(true)}
    }
    fun toggleLightMode()
    {
        viewModelScope.launch { repository.setDarkMode(false)}
    }

    fun toggleSystemMode()
    {
        viewModelScope.launch {repository.setSystemMode()}
    }

}


